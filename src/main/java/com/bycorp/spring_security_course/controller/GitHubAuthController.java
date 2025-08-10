package com.bycorp.spring_security_course.controller;

import com.bycorp.spring_security_course.service.auth.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class GitHubAuthController {

    private final JwtService jwtService;
    @Value("${GITHUB_CLIENT_ID}")
    private String clientId;

    @Value("${GITHUB_CLIENT_SECRET}")
    private String clientSecret;

    public GitHubAuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/login/github")
    public void redirectToGitHub(HttpServletResponse response) throws IOException {
        String redirectUri = "http://localhost:9191/oauth/callback/github";
        String githubAuthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=user:email";
        response.sendRedirect(githubAuthUrl);
    }

    @GetMapping("/callback/github")
    public ResponseEntity<?> githubCallback(@RequestParam String code) {
        // 1. Intercambiar el code por el access token
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, request, Map.class);
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No se pudo obtener el access token de GitHub"));
        }

        // 2. Obtener datos del usuario
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse =
                restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, userRequest, Map.class);

        Map<String, Object> githubUser = userResponse.getBody();
        String username = githubUser.get("login").toString();

        // 3. Crear UserDetails temporal
        UserDetails tempUser = User.withUsername(username)
                .password("") // No importa porque no lo vamos a usar
                .authorities("ROLE_USER") //rol por defecto
                .build();

        // 4. Extra claims opcionales (puedes meter info de GitHub)
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("github_id", githubUser.get("id"));
        extraClaims.put("avatar_url", githubUser.get("avatar_url"));

        // 5. Generar tu JWT con tu método
        String jwt = jwtService.generateToken(tempUser, extraClaims);

        // 6. Responder al cliente
//        return ResponseEntity.ok(Map.of(
//                "jwt", jwt,
//                "githubData", githubUser
//        ));
        return ResponseEntity.ok(Map.of("jwt", jwt));
    }

}
