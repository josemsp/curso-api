package com.learnig_spring_boot.curso_api.user.controller;

import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.exception.EmailAlreadyExistsException;
import com.learnig_spring_boot.curso_api.user.exception.UserNotFoundException;
import com.learnig_spring_boot.curso_api.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

import static org.mockito.Mockito.*;
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
}
