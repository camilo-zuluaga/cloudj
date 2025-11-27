package com.cloudj.backend.exceptions;

public class JWTException extends RuntimeException {
    public JWTException(String message) {
        super(message);
    }
}
