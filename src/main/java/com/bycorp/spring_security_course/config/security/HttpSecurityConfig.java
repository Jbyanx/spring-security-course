package com.bycorp.spring_security_course.config.security;

import com.bycorp.spring_security_course.config.security.filter.JwtAuthenticationFilter;
import com.bycorp.spring_security_course.persistence.util.Role;
import com.bycorp.spring_security_course.persistence.util.RolePermission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
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
                .authorizeHttpRequests(authorizeRequests -> {
                    buildrequestMatcher(authorizeRequests);
                })

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    private static void buildrequestMatcher(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
        /*
        * Autorizacion de endpoints de productos
        * */
        authorizeRequests.requestMatchers(HttpMethod.GET, "/products")
                .hasAuthority(RolePermission.READ_ALL_PRODUCTS.name());

        authorizeRequests.requestMatchers(HttpMethod.GET, "/products/{id}")
                .hasAuthority(RolePermission.READ_ONE_PRODUCT.name());

        authorizeRequests.requestMatchers(HttpMethod.POST, "/products")
                .hasAuthority(RolePermission.CREATE_ONE_PRODUCT.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/products/{id}")
                .hasAuthority(RolePermission.UPDATE_ONE_PRODUCT.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/products/{id}/disable")
                .hasAuthority(RolePermission.DISABLE_ONE_PRODUCT.name());
        /*
        * Autorizacion de endpoints de categorias
        * */
        authorizeRequests.requestMatchers(HttpMethod.GET, "/categories")
                .hasAuthority(RolePermission.READ_ALL_CATEGORIES.name());

        authorizeRequests.requestMatchers(HttpMethod.GET, "/categories/{id}")
                .hasAuthority(RolePermission.READ_ONE_CATEGORY.name());

        authorizeRequests.requestMatchers(HttpMethod.POST, "/categories")
                .hasAuthority(RolePermission.CREATE_ONE_CATEGORY.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/categories/{id}")
                .hasAuthority(RolePermission.UPDATE_ONE_CATEGORY.name());

        authorizeRequests.requestMatchers(HttpMethod.PUT, "/products/{id}/disable")
                .hasAuthority(RolePermission.DISABLE_ONE_CATEGORY.name());

        /*
         * Autorizacion de endpoints del perfil
         * */
        authorizeRequests.requestMatchers(HttpMethod.GET, "/auth/profile")
                        .hasAuthority(RolePermission.READ_MY_PROFILE.name());

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
}
