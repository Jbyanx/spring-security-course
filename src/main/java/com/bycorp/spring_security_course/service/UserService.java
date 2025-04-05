package com.bycorp.spring_security_course.service;

import com.bycorp.spring_security_course.dto.request.SaveUser;
import com.bycorp.spring_security_course.dto.response.GetUser;
import com.bycorp.spring_security_course.persistence.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User registerOneCustomer(@Valid SaveUser newUser);
    Optional<User> findOneByUsername(String username);

    Page<GetUser> findAll(Pageable pageable);
}
