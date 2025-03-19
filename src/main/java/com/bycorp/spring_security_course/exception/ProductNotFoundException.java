package com.bycorp.spring_security_course.exception;

public class ProductNotFoundException extends RuntimeException {
    private String message;

    public ProductNotFoundException(String message) {
        super(message);
    }
}
