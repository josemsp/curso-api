package com.learnig_spring_boot.curso_api.user.controller;

import com.learnig_spring_boot.curso_api.user.dto.CreateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UpdateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UserPageResponse;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

/**
 * Rest controller responsible for handling user HTTP requests.
 *
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "email",
            "createdAt"
    );

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

    /**
     * Retrieves users with pagination and sorting.
     *
     * @param pageable pagination and sorting configuration
     * @return paginated list of users
     * */
    @GetMapping
    public ResponseEntity<UserPageResponse> getUsers(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ){
//        prevent clients from requesting excessively large pages.
        if(pageable.getPageSize() > MAX_PAGE_SIZE){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size cannot exceed: " + MAX_PAGE_SIZE
            );
        }

//        only allow sorting by fields exposed by our API.
        boolean hasInvalidSortField = pageable.getSort()
                .stream()
                .anyMatch(
                        order -> !ALLOWED_SORT_FIELDS.contains(order.getProperty())
                );

        if(hasInvalidSortField){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field. Allowed fields: " + ALLOWED_SORT_FIELDS
            );
        }

        UserPageResponse response = userService.getUsers(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing user.
     *
     * @param id the identifier of the user
     * @param request the new user information
     * @return the updated user
     * */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ){
        UserResponse response = userService.updateUser(id, request);

        return ResponseEntity.ok(response);
    }
}
