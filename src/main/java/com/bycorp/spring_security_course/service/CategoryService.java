package com.bycorp.spring_security_course.service;

import com.bycorp.spring_security_course.dto.request.SaveCategory;
import com.bycorp.spring_security_course.dto.response.GetCategory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<GetCategory> getAll(Pageable pageable);

    GetCategory getById(Long id);

    GetCategory save(@Valid SaveCategory category);

    GetCategory updateById(@Valid SaveCategory category, Long id);

    GetCategory disableById(Long id);
}
