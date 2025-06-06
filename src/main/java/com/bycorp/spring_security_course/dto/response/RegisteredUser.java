package com.bycorp.spring_security_course.dto.response;

import java.io.Serializable;

public record RegisteredUser(
        Long id,
        String username,
        String name,
        String role,
        String jwt
) implements Serializable {
}
