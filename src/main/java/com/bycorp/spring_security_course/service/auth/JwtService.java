package com.bycorp.spring_security_course.service.auth;

import com.bycorp.spring_security_course.persistence.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.cert.X509CertSelector;
import java.util.Date;
import java.util.Map;

@Service
@Log4j2
public class JwtService {
    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;
    @Value("${security.jwt.secret-key}")
    private String  SECRET_KEY;

    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + (EXPIRATION_IN_MINUTES * 60 * 1000));
        String jwt = Jwts.builder()
                .header()
                    .type("JWT")
                .and()

                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .claims(extraClaims)

                .signWith(generateKey(), Jwts.SIG.HS256) //recuerda el secretkey
                .compact();
        return jwt;
    }

    private SecretKey generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        log.info("password decoded "+passwordDecoded);
        return Keys.hmacShaKeyFor(passwordDecoded);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(generateKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token, User user){
        return user.getUsername().equals(extractUsername(token));
    }
}
