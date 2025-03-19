package com.bycorp.spring_security_course.dto.response;

import com.bycorp.spring_security_course.persistence.entity.Product;

import java.math.BigDecimal;

public record GetProduct(
        Long id,
        String name,
        BigDecimal price,
        Product.ProductStatus status,
        String category
) {
}
