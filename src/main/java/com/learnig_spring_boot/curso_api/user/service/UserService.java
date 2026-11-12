package com.learnig_spring_boot.curso_api.user.service;

import com.learnig_spring_boot.curso_api.user.dto.CreateUserRequest;
import com.learnig_spring_boot.curso_api.user.dto.UserResponse;
import com.learnig_spring_boot.curso_api.user.entity.User;
import com.learnig_spring_boot.curso_api.user.exception.EmailAlreadyExistsException;
import com.learnig_spring_boot.curso_api.user.exception.UserNotFoundException;
import com.learnig_spring_boot.curso_api.user.repository.UserRepository;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
