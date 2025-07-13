package com.bycorp.spring_security_course.config.security.filter;

import com.bycorp.spring_security_course.persistence.entity.JwtToken;
import com.bycorp.spring_security_course.persistence.repository.JwtTokenRepository;
import com.bycorp.spring_security_course.service.UserService;
import com.bycorp.spring_security_course.service.auth.JwtService;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtTokenRepository jwtTokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService, JwtTokenRepository jwtTokenRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //obtener authorization header
        String jwt = jwtService.extractJwtFromRequest(request);

        //obtener token
        if(jwt == null || !StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        //2.1 Obtener token no expirado y valido desde DB
        Optional<JwtToken> token = jwtTokenRepository.findByToken(jwt);
        boolean isValid = validateToken(token);

        if(!isValid){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //3. Obtener el subject/username desde el token, esta acción a su vez valida el token
            String username = jwtService.extractUsername(jwt);

            //4. Settear objeto authentication dentro de SecurityContextHolder
            UserDetails user = userService.findOneByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username));

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, user.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            //5. Ejecutar el resto de filtros
            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            System.out.println("JWT Token expirado: " + ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token expirado",
                    "El token JWT ha expirado. Por favor, inicia sesión nuevamente.");
            return;

        } catch (io.jsonwebtoken.JwtException ex) {
            System.out.println("JWT Token inválido: " + ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token inválido",
                    "El token JWT es inválido o está malformado.");
            return;

        } catch (UsernameNotFoundException ex) {
            System.out.println("Usuario no encontrado: " + ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Usuario no encontrado",
                    "El usuario asociado al token no existe.");
            return;

        } catch (Exception ex) {
            System.out.println("Error procesando JWT token: " + ex.getMessage());
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                    "Error procesando la autenticación.");
            return;
        }
    }

    private boolean validateToken(Optional<JwtToken> token) {
        if(!token.isPresent()) {
            System.out.println("token no existe o no fue generado en nuestro sistema");
            return false;
        }

        JwtToken jwtToken = token.get();
        Date now = new Date(System.currentTimeMillis());
        boolean isValid = jwtToken.isValid() && jwtToken.getExpiration().after(now);
        if(!isValid) {
            System.out.println("Token invalido o no fue creado en nuestro sistema");
            updateTokenStatus(jwtToken);
        }
        return isValid;
    }

    private void updateTokenStatus(JwtToken jwtToken) {
        jwtToken.setValid(false);
        jwtTokenRepository.save(jwtToken);
    }

    private void sendErrorResponse(HttpServletResponse response,
                                   HttpStatus status,
                                   String error,
                                   String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("status", status.value());
        errorResponse.put("message", message);
        errorResponse.put("timestamp", Instant.now().toString());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}