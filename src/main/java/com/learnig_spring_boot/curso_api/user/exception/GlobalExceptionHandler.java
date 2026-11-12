package com.learnig_spring_boot.curso_api.user.exception;

import com.learnig_spring_boot.curso_api.user.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Handles application exceptions globally.
 *
 * This class centralizes the conversion of exceptions
 * into standardized HTTP responses
 * */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles attempts to register an existing email.
     *
     * @param exception business exception
     * @param request current HTTP request
     * @return HTTP 409 Conflict
     * */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception,
            HttpServletRequest request
    ){
        ApiErrorResponse error = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    /**
     * Handles validation errors from incoming requests.
     *
     * @param exception validation exception
     * @param request current HTTP request
     * @return HTTP 400 Bad Request
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ){
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorResponse error = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    /**
     * Handles requests for users that do not exist.
     *
     * @param exception user not found exception
     * @param request current HTTP request
     * @return HTTP 404 Not Found
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUSerNotFound(
            UserNotFoundException exception,
            HttpServletRequest request
    ){
      ApiErrorResponse error = new ApiErrorResponse(
              Instant.now(),
              HttpStatus.NOT_FOUND.value(),
              HttpStatus.NOT_FOUND.getReasonPhrase(),
              exception.getMessage(),
              request.getRequestURI()
      );

      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(error);
    }
}
