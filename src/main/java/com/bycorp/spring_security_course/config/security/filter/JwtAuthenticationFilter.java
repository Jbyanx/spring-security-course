package com.bycorp.spring_security_course.config.security.filter;

import com.bycorp.spring_security_course.service.UserService;
import com.bycorp.spring_security_course.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("entro el el filtro JwtAuthenticationfilter");
        //1. Obtenr encabezado hhtp Authotizathion
        String authorizationHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //2. Obtenr jwt desde el encabezado
        String jwt = authorizationHeader.substring(7);
        //3. obtener el subject/username desde el token, esta accion a su vez valida el token
        String username = jwtService.extractUsername(jwt);
        //4. settear objeto authentication dentro de SecurityContextHolder
        UserDetails user = userService.findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(
                authToken
        );
        //5. ejecutar el resto de filtros
        filterChain.doFilter(request, response);
    }
}
