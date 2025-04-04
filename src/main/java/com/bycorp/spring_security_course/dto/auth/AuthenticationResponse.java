package com.bycorp.spring_security_course.dto.auth;

import java.io.Serializable;

public record AuthenticationResponse(
        String jwt
) implements Serializable {
}
