package com.bycorp.spring_security_course.dto.request;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record SaveUser(
        @Size(min = 4)
        String name,
        @Size(min = 4)
        String username,
        @Size(min = 8)
        String password,
        @Size(min = 8)
        String repeatedPassword
) implements Serializable {
}
