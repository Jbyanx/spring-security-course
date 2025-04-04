package com.bycorp.spring_security_course.config.security;

import com.bycorp.spring_security_course.config.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig {
    private final AuthenticationProvider daoAuthenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public HttpSecurityConfig(AuthenticationProvider daoAuthenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(config -> config.disable()) //se usa es en statefull por eso lo deshabilitamos
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthenticationProvider)
                .authorizeHttpRequests(authorizeRequests ->{
                    authorizeRequests.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.GET,"/auth/validate").permitAll();
                    authorizeRequests.requestMatchers("/error").permitAll();
                    authorizeRequests.anyRequest().authenticated();
                })

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
