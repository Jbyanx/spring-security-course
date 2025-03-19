package com.bycorp.spring_security_course.dto.response;

import java.io.Serializable;

public record ApiError(
        String message,
        Integer status,
        String backendMessage
) implements Serializable {
}
