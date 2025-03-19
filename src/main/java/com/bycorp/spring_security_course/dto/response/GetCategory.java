package com.bycorp.spring_security_course.dto.response;

import com.bycorp.spring_security_course.persistence.entity.Category;

public record GetCategory(
        Long id,
        String name,
        Category.CategoryStatus status
) {
}
