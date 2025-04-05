package com.bycorp.spring_security_course.exception;

public class InvalidPasswordException extends RuntimeException {
    private String message;

    public InvalidPasswordException(String message) {
        super(message);
    }
}
