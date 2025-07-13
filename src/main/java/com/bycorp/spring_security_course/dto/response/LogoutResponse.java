package com.bycorp.spring_security_course.dto.response;

import java.io.Serializable;

public record LogoutResponse(
        String message
) implements Serializable {
}
