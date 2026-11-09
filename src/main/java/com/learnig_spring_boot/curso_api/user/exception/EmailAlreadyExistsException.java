package com.learnig_spring_boot.curso_api.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email){
        super("Email already registered: " + email);
    }
}
