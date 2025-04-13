package com.bycorp.spring_security_course.config.security;

import com.bycorp.spring_security_course.config.security.filter.JwtAuthenticationFilter;
import com.bycorp.spring_security_course.persistence.util.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
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
//                .authorizeHttpRequests(authorizeRequests -> {
//                    buildrequestMatcherv2(authorizeRequests);
//                })

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    private static void buildrequestMatcher(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
        /*
         * Autorizacion de endpoints de productos
         * */
        authorizeRequests.requestMatchers(HttpMethod.GET, "/products")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.GET, "/products/{id}")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.POST, "/products")
                .hasRole(Role.ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/products/{id}")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/products/{id}/disable")
                .hasRole(Role.ADMINISTRATOR.name());
        /*
         * Autorizacion de endpoints de categorias
         * */
        authorizeRequests.requestMatchers(HttpMethod.GET, "/categories")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.GET, "/categories/{id}")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.POST, "/categories")
                .hasRole(Role.ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/categories/{id}")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/products/{id}/disable")
                .hasRole(Role.ADMINISTRATOR.name());

        /*
         * Autorizacion de endpoints del perfil
         * */
        authorizeRequests.requestMatchers(HttpMethod.GET, "/auth/profile")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.ASSISTANT_ADMINISTRATOR.name(),
                        Role.CUSTOMER.name());

        /*
         * Autorizacion de endpoints de customer
         * */
        //SOLO A MODO DE PRUEBA TODO
        authorizeRequests.requestMatchers(HttpMethod.GET, "/customers").permitAll();
        authorizeRequests.requestMatchers(HttpMethod.POST, "/customers").permitAll();

        //Autorizacion de endpoints publicos
        authorizeRequests.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
        authorizeRequests.requestMatchers(HttpMethod.GET,"/auth/validate").permitAll();
        authorizeRequests.requestMatchers("/error").permitAll();

        authorizeRequests.anyRequest().authenticated();
    }


    private static void buildrequestMatcherv2(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
        //Autorizacion de endpoints publicos
        authorizeRequests.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
        authorizeRequests.requestMatchers(HttpMethod.GET,"/auth/validate").permitAll();
        authorizeRequests.requestMatchers("/error").permitAll();

        authorizeRequests.anyRequest().authenticated();
    }
}
