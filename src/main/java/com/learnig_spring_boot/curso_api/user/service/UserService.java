package com.learnig_spring_boot.curso_api.user.service;

import com.learnig_spring_boot.curso_api.user.dto.CreateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UpdateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UserPageResponse;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.entity.User;
import com.learnig_spring_boot.curso_api.user.exception.EmailAlreadyExistsException;
import com.learnig_spring_boot.curso_api.user.exception.UserNotFoundException;
import com.learnig_spring_boot.curso_api.user.repository.UserRepository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request){
//        Normalize the email to avoid case and whitespace inconsistencies
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

//        Check whether the normalized email already exists
        if(userRepository.existsByEmail(normalizedEmail)){
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        User user = new User(request.getName(), normalizedEmail);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    /**
     * Retrieves a user by its identifier.
     *
     * @param id identifier of the requested user
     * @return user information
     * @throws UserNotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id){
//        Find the user or throw an exception if it does not exist.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

//        Convert the entity into a response DTO
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    /**
     * Retrieves users using pagination and sorting.
     *
     * @param pageable pagination and sorting configuration
     * @return a paginated response containing users
     * */
    public UserPageResponse getUsers(Pageable pageable){
//        ask the repository to retrieve only the requested page.
        Page<User> usersPage = userRepository.findAll(pageable);

//        Convert database entities into response DTOs.
        List<UserResponse> users = usersPage.getContent()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt()
                ))
                .toList();

//        build our API response with pagination metadata.
        return new UserPageResponse(
                users,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isFirst(),
                usersPage.isLast()
        );
    }

    /**
     * Updates an existing user's information
     *
     * @param id the identifier ot the user to update
     * @param request the new user information
     * @return the updated user
     * @throws UserNotFoundException if the user does not exist
     * @throws EmailAlreadyExistsException if the email belongs to another user
     * */
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request){
        // Find the existing user or throw an exception.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Normalize the input data.
        String normalizedName = request.getName().trim();
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // Check whether another user already uses this email.
        if(userRepository.existsByEmailAndIdNot(normalizedEmail, id)){
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        // Update the existing entity.
        user.updateDetails(normalizedName, normalizedEmail);

        // Convert the updated entity into a response DTO.
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
