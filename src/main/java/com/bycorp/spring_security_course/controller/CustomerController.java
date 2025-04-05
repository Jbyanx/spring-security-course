package com.bycorp.spring_security_course.controller;

import com.bycorp.spring_security_course.dto.request.SaveUser;
import com.bycorp.spring_security_course.dto.response.GetUser;
import com.bycorp.spring_security_course.dto.response.RegisteredUser;
import com.bycorp.spring_security_course.service.UserService;
import com.bycorp.spring_security_course.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public CustomerController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<RegisteredUser> registerOne(@RequestBody @Valid SaveUser newUser){
        RegisteredUser registeredUser = authenticationService.registerOneCustomer(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping
    public ResponseEntity<Page<GetUser>> getAllUsers(Pageable pageable){
        return ResponseEntity.ok(userService.findAll(pageable));
    }
}
