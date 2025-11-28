package com.cloudj.backend.exceptions;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
