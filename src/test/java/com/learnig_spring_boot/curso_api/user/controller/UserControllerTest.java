package com.learnig_spring_boot.curso_api.user.controller;

import com.learnig_spring_boot.curso_api.user.dto.UserPageResponse;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.exception.EmailAlreadyExistsException;
import com.learnig_spring_boot.curso_api.user.exception.UserNotFoundException;
import com.learnig_spring_boot.curso_api.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
//        arrange
        UserResponse response = new UserResponse(
                1L,
                "José Salazar",
                "jose@example.com",
                Instant.parse("2026-11-07T20:00:00Z")
        );

        when(userService.createUser(any()))
                .thenReturn(response);

//        Act and assert
        mockMvc
                .perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "José Salazar",
                                    "email": "jose@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("José Salazar"))
                .andExpect(jsonPath("$.email").value("jose@example.com"))
                .andExpect(jsonPath("$.createdAt").value("2026-11-07T20:00:00Z"));

        verify(userService).createUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
//        Act and Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                              "name": "José Manuel",
                              "email": "invalid-email"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("email")));

        // Service should not be called because validation failed.
        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

        // Arrange
        when(userService.createUser(any()))
                .thenThrow(new EmailAlreadyExistsException(
                        "jose@example.com"
                ));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "José Manuel",
                                    "email": "jose@example.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Email already registered: jose@example.com"));
    }

//    Tests for get user
    @Test
    void shouldReturnUserWhenIdExists() throws Exception {
//        Arrange
        UserResponse userResponse = new UserResponse(
                1L,
                "José Salazar",
                "jose@example.com",
                Instant.parse("2026-11-08T20:00:00Z")
        );

        when(userService.getUserById(1L))
                .thenReturn(userResponse);

//        Act and assert
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/users/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("José Salazar"))
                .andExpect(jsonPath("$.email").value("jose@example.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDoesNotExists() throws Exception {
//        Arrange
        when(userService.getUserById(999L))
                .thenThrow(new UserNotFoundException(999L));

//        Act & assert
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/users/999")
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void shouldReturnPaginatedUsers() throws Exception{
//        Arrange
        UserResponse user1 = new UserResponse(
                1L,
                "Ana",
                "ana@example.com",
                Instant.parse(("2026-11-12T10:00:00Z"))
        );

        UserResponse user2 = new UserResponse(
                2L,
                "Carlos",
                "carlos@example.com",
                Instant.parse("2026-11-12T10:00:00Z")
        );

        UserPageResponse response = new UserPageResponse(
                List.of(user1, user2),
                0,
                10,
                12,
                2,
                true,
                false
        );

        when(userService.getUsers(any(Pageable.class)))
                .thenReturn(response);

//        act & assert
        mockMvc.perform(get("/api/v1/users")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "name,asc")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Ana"))
                .andExpect(jsonPath("$.content[1].name").value("Carlos"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void shouldRejectPageSizeGreaterThanMaximum() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldRejectInvalidSortField() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .param("sort", "password,asc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
