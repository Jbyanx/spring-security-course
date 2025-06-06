package com.bycorp.spring_security_course.dto.auth;

import java.io.Serializable;

public record AuthenticationRequest(
    String username,
    String password
) implements Serializable {
}
