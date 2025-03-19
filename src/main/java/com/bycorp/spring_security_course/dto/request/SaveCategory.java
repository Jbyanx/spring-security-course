package com.bycorp.spring_security_course.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record SaveCategory(
        @NotBlank
        String name
) implements Serializable {
}
