package com.bycorp.spring_security_course.dto.response;

import java.io.Serializable;

public record GetUser(
        String username,
        String  role,
        int passwordReference
) implements Serializable {
}
