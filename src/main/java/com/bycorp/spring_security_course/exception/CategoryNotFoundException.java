package com.bycorp.spring_security_course.exception;

public class CategoryNotFoundException extends RuntimeException {
    private String message;

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
