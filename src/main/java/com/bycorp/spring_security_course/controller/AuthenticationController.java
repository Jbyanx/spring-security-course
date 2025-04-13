package com.bycorp.spring_security_course.controller;

import com.bycorp.spring_security_course.dto.auth.AuthenticationRequest;
import com.bycorp.spring_security_course.dto.auth.AuthenticationResponse;
import com.bycorp.spring_security_course.persistence.entity.User;
import com.bycorp.spring_security_course.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest){

        return ResponseEntity.ok(authenticationService.login(authenticationRequest));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token){
        return ResponseEntity.ok(authenticationService.validateToken(token));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getAuthenticatedUser(){
        User user = authenticationService.getAuthenticatedUser();
        return ResponseEntity.ok(user);
    }
}
