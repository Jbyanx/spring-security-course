package com.bycorp.spring_security_course.service.auth;

import com.bycorp.spring_security_course.dto.auth.AuthenticationRequest;
import com.bycorp.spring_security_course.dto.auth.AuthenticationResponse;
import com.bycorp.spring_security_course.dto.request.SaveUser;
import com.bycorp.spring_security_course.dto.response.RegisteredUser;
import com.bycorp.spring_security_course.exception.UserNotFoundException;
import com.bycorp.spring_security_course.persistence.entity.JwtToken;
import com.bycorp.spring_security_course.persistence.entity.User;
import com.bycorp.spring_security_course.persistence.repository.JwtTokenRepository;
import com.bycorp.spring_security_course.persistence.repository.UserRepository;
import com.bycorp.spring_security_course.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;

    public AuthenticationService(
            UserService userService, JwtService jwtService,
            AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenRepository jwtTokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("authorities", user.getAuthorities());
        return extraClaims;
    }

    @PreAuthorize("permitAll()")
    @Transactional
    public RegisteredUser registerOneCustomer(@Valid SaveUser newUser) {
        User user = userService.registerOneCustomer(newUser);
        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        saveUserToken(user, jwt);

        RegisteredUser userDto = new RegisteredUser(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole().name(),
                jwt
        );
        return userDto;
    }

    @PreAuthorize("permitAll()")
    @Transactional
    public AuthenticationResponse login(@Valid AuthenticationRequest authenticationRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.username(),
                authenticationRequest.password()
        );
        authenticationManager.authenticate(authentication);

        UserDetails user = userService.findOneByUsername(authenticationRequest.username()).get();
        String jwt = jwtService.generateToken(user, generateExtraClaims((User) user));
        saveUserToken((User) user, jwt);

        return new AuthenticationResponse(jwt);
    }

    private void saveUserToken(User user, String jwt) {
        JwtToken token = new JwtToken();
        token.setToken(jwt);
        token.setUser(user);
        token.setExpiration(jwtService.extractExpiration(jwt));
        token.setValid(true);

        jwtTokenRepository.save(token);
    }

    @PreAuthorize("permitAll()")
    public Boolean validateToken(String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("user not found validating token, username: "+username));
        return jwtService.isValid(token, user);
    }

    @PreAuthorize("hasAuthority('READ_MY_PROFILE')")
    public User getAuthenticatedUser() {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String authenticatedUser = auth.getPrincipal().toString();

        return userRepository.findByUsername(authenticatedUser)
                .orElseThrow(() -> new UserNotFoundException(authenticatedUser+" not found"));
    }

    public void logout(HttpServletRequest request) {
        String jwt = jwtService.extractJwtFromRequest(request);

        if(jwt == null || !StringUtils.hasText(jwt)) {
            return;
        }

        Optional<JwtToken> token = jwtTokenRepository.findByToken(jwt);

        if(token.isPresent() && token.get().isValid()) {
            token.get().setValid(false);
            jwtTokenRepository.save(token.get());
        }
    }
}
