package com.bycorp.spring_security_course.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.math.BigDecimal;

public record SaveProduct(
        @NotBlank
        String name,
        @DecimalMin(value = "0.01")
        BigDecimal price,
        @Min(1)
        Long categoryId
) implements Serializable {
}
