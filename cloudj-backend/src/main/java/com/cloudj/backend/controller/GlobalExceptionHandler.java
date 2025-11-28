package com.cloudj.backend.controller;

import com.cloudj.backend.dto.out.ErrorResponse;
import com.cloudj.backend.exceptions.AuthException;
import com.cloudj.backend.exceptions.JWTException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponse> handleAWSS3Exception(S3Exception ex,
                                                              HttpServletRequest request) {
        log.error("S3 Operation failed: %s - %s"
                .formatted(ex.awsErrorDetails().errorCode(),
                        ex.awsErrorDetails().errorMessage()
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.awsErrorDetails().errorCode())
                .errorMessage(ex.awsErrorDetails().errorMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex,
                                                             HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("AUTH_ERROR")
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<ErrorResponse> handleJWTException(JWTException ex,
                                                            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("JWT_TOKEN_ERROR")
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
