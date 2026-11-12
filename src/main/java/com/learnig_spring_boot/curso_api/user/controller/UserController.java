package com.learnig_spring_boot.curso_api.user.controller;

import com.learnig_spring_boot.curso_api.user.dto.CreateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller responsible for handling user HTTP requests.
 *
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    /**
     * Constructor injection.
     *
     * @param userService service responsible for user business logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param request user creation data
     * @return HTTP 201 with the created user
     *
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves a user by its identifier.
     *
     * @param id identifier of the requested user
     * @return HTTP 200 with the requested user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {
        UserResponse response = userService.getUserById(id);

        return ResponseEntity
                .ok(response);
    }
}
