package com.learnig_spring_boot.curso_api.user.service;

import com.learnig_spring_boot.curso_api.user.dto.CreateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.entity.User;
import com.learnig_spring_boot.curso_api.user.exception.EmailAlreadyExistsException;
import com.learnig_spring_boot.curso_api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest request;

//    execute before each test
    @BeforeEach
    void setUp() {
        request = new CreateUserRequest();
        request.setName("José Salazar");
        request.setEmail("jose@example.com");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        User savedUser = new User(request.getName(), request.getEmail());

        Instant createdAt = Instant.now();

//        Simulate the values assigned by the database and JPA
        setUserId(savedUser, 1L);
        setCreatedAt(savedUser, createdAt);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

//        Act
        UserResponse response = userService.createUser(request);

//        Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("José Salazar", response.getName());
        assertEquals("jose@example.com", response.getEmail());
        assertEquals(createdAt, response.getCreatedAt());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists(){
//        arrange
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

//act and assert
        assertThrows(EmailAlreadyExistsException.class, ()-> userService.createUser(request));

        verify(userRepository).existsByEmail(request.getEmail());
//        verify that no user is saved
        verify(userRepository, never()).save(any(User.class));
    }

    private void setUserId(User user, Long id){
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (ReflectiveOperationException exception){
            throw new RuntimeException(exception);
        }
    }

    private void setCreatedAt(User user, Instant createdAt){
        try {
            var field = User.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(user, createdAt);
        } catch (ReflectiveOperationException exception){
            throw new RuntimeException(exception);
        }
    }
}
