package com.bycorp.spring_security_course.service;

import com.bycorp.spring_security_course.dto.request.SaveProduct;
import com.bycorp.spring_security_course.dto.response.GetProduct;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    GetProduct save(SaveProduct product);
    GetProduct getById(Long id);
    GetProduct getByName(String name);
    Page<GetProduct> getAll(Pageable pageable);

    GetProduct updateById(@Valid SaveProduct product, Long id);

    GetProduct disableById(Long id);
}
