package com.learnig_spring_boot.curso_api.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found with id: "+id);
    }
}
