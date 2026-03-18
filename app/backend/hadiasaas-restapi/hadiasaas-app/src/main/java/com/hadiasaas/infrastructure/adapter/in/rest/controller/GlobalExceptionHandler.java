package com.hadiasaas.infrastructure.adapter.in.rest.controller;


import com.hadiasaas.domain.exceptions.*;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.ValidationErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Translates domain and validation exceptions into structured HTTP error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleInvalidImage(ResponseStatusException ex) {
        logError(ex);
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Refresh Token", ex.getMessage());
    }

    @ExceptionHandler(TwoFactorSetupRequiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ValidationErrorResponseDTO> handleTwoFactorSetupRequired(TwoFactorSetupRequiredException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.FORBIDDEN, "2FA Setup Required", ex.getMessage());
    }

    @ExceptionHandler(AuthFunctionalException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleAuthenticationExceptions(AuthFunctionalException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication Error", ex.getMessage());
    }

    @ExceptionHandler(FunctionalException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleUserException(FunctionalException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Business Error", ex.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> IllegalArgumentException(IllegalArgumentException ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logError(ex);

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ?
                    ((FieldError) error).getField() :
                    error.getObjectName();

            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponseDTO errorResponse = new ValidationErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ValidationErrorResponseDTO> buildErrorResponse(
            HttpStatus status,
            String title,
            String message
    ) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", message);

        ValidationErrorResponseDTO errorResponse = new ValidationErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                title,
                errors
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ValidationErrorResponseDTO> handleAlreadyExists(UserAlreadyExistsException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.CONFLICT, "User Error", ex.getMessage());
    }

    @ExceptionHandler(TwoFactorAlreadyEnabledException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ValidationErrorResponseDTO> handleTwoFactorAlreadyEnabled(TwoFactorAlreadyEnabledException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.CONFLICT, "2FA Error", ex.getMessage());
    }

    @ExceptionHandler(RoleGroupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ValidationErrorResponseDTO> handleRoleGroupNotFound(RoleGroupNotFoundException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Role Group Error", ex.getMessage());
    }

    @ExceptionHandler(RoleGroupNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ValidationErrorResponseDTO> handleRoleGroupNameConflict(RoleGroupNameAlreadyExistsException ex) {
        logError(ex);
        return buildErrorResponse(HttpStatus.CONFLICT, "Role Group Error", ex.getMessage());
    }

    private static void logError(Exception ex) {
        log.error("Error occurred: {}", ex.getMessage(), ex);
    }

}
