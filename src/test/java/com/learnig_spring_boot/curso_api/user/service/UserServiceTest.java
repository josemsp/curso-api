package com.learnig_spring_boot.curso_api.user.service;

import com.learnig_spring_boot.curso_api.user.dto.CreateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UpdateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UserPageResponse;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.entity.User;
import com.learnig_spring_boot.curso_api.user.exception.EmailAlreadyExistsException;
import com.learnig_spring_boot.curso_api.user.exception.UserNotFoundException;
import com.learnig_spring_boot.curso_api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        when(userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail()))
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

        verify(userRepository).existsByEmailAndDeletedAtIsNull(request.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists(){
//        arrange
        when(userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail()))
                .thenReturn(true);

//act and assert
        assertThrows(EmailAlreadyExistsException.class, ()-> userService.createUser(request));

        verify(userRepository).existsByEmailAndDeletedAtIsNull(request.getEmail());
//        verify that no user is saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldNormalizeEmailBeforeSaving(){
        when(userRepository.existsByEmailAndDeletedAtIsNull("jose@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return user;
                });

        userService.createUser(createUserRequest(
                "José Salazar",
                "JOSE@EXAMPLE.COM"
        ));

        verify(userRepository).existsByEmailAndDeletedAtIsNull("jose@example.com");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("jose@example.com")
        ));
    }

    @Test
    void shouldReturnUserWhenIdExists(){
        Long userId = 1L;
        User user = new User(
                "José Salazar",
                "jose@example.com"
        );

        when(userRepository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(java.util.Optional.of(user));

//        Act
        UserResponse response = userService.getUserById(userId);

//        Assert
        assertNotNull(response);
        assertEquals("José Salazar", response.getName());
        assertEquals("jose@example.com", response.getEmail());

        verify(userRepository).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExists() {
//        Arrange
        Long userId = 999L;

        when(userRepository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.empty());

//        Act & assert
        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        verify(userRepository).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    void shouldUpdateUserSuccessfully(){
//        arrange
        Long userId = 1L;
        User user = new User("Old name", "old@example.com");
        UpdateUserRequest request = new UpdateUserRequest(
                "New name",
                "new@example.com"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNotAndDeletedAtIsNull(
                "new@example.com",
                userId
        ))
                .thenReturn(false);

//        act
        UserResponse userResponse = userService.updateUser(
                userId,
                request
        );

//        assert
        assertEquals("New name", userResponse.getName());
        assertEquals("new@example.com", userResponse.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNotAndDeletedAtIsNull("new@example.com", userId);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser(){
//        arrange
        Long userId = 999L;
        UpdateUserRequest request = new UpdateUserRequest(
                "New name",
                "new@example.com"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

//        act & assert
        assertThrows(
                UserNotFoundException.class,
                ()-> userService.updateUser(userId, request)
        );

        verify(userRepository, never())
                .existsByEmailAndIdNotAndDeletedAtIsNull(
                        "new@example.com",
                        userId
                );
    }

    @Test
    void shouldThrowExceptionWhenEmailBelongsToAnotherUser(){
//        arrange
        Long userId = 1L;
        User user = new User(
                "old name", "old@example.com"
        );
        UpdateUserRequest request = new UpdateUserRequest(
                "new name", "taken@example.com"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNotAndDeletedAtIsNull("taken@example.com", userId))
                .thenReturn(true);

//        act & assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.updateUser(userId, request)
        );

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNotAndDeletedAtIsNull("taken@example.com", userId);
    }

    @Test
    void shouldNormalizeNameAndEmailWhenUpdatingUser(){
//        arrange
        Long userId = 1L;
        User user = new User("old name", "old@example.com");
        UpdateUserRequest request = new UpdateUserRequest(
                "new name",
                "new@example.com"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNotAndDeletedAtIsNull("new@example.com", userId))
                .thenReturn(false);

//        act
        UserResponse response = userService.updateUser(
                userId, request
        );

//        assert
        assertEquals("new name", response.getName());
        assertEquals("new@example.com", response.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNotAndDeletedAtIsNull("new@example.com", userId);
    }

    @Test
    void shouldSoftDeleteUser(){
//        arrange
        Long userId = 1L;
        User user = new User(
                "José Salazar",
                "jose@example.com"
        );

        when(userRepository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

//        act
        userService.deleteUser(userId);

//        assert
        assertNotNull(user.getDeletedAt());

        verify(userRepository).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser(){
//        arrange
        Long userId = 999L;

        when(userRepository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.empty());

//        act and assert
        assertThrows(UserNotFoundException.class, ()-> userService.deleteUser(userId));
    }

    @Test
    void shouldReturnPaginatedActiveUsers() {
//        arrange
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = new User("Ana", "ana@example.com");
        setUserId(user1, 1L);
        setCreatedAt(user1, Instant.now());

        User user2 = new User("Carlos", "carlos@example.com");
        setUserId(user2, 2L);
        setCreatedAt(user2, Instant.now());

        Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAllByDeletedAtIsNull(pageable))
                .thenReturn(page);

//        act
        UserPageResponse response = userService.getUsers(pageable);

//        assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("Ana", response.getContent().get(0).getName());
        assertEquals("Carlos", response.getContent().get(1).getName());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(2, response.getTotalElements());

        verify(userRepository).findAllByDeletedAtIsNull(pageable);
    }

//    Methods
    private CreateUserRequest createUserRequest(String name, String email){
        CreateUserRequest request = new CreateUserRequest();
        request.setName(name);
        request.setEmail(email);
        return request;
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
