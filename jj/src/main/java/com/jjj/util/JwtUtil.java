package com.jjj.util;

import java.sql.Date;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    private final String base64Secret;
    private final int accessExpirationMinutes;
    private SecretKey secretKey;

    public JwtUtil(
        @Value("${jwt.secret:}") 
        String base64Secret,
        @Value("${jwt.access-expiration-minutes:30}")
        int accessExpirationMinutes
    ) {
        this.base64Secret = base64Secret;
        this.accessExpirationMinutes = accessExpirationMinutes;
    }

    @PostConstruct
    private void init() 
    {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalStateException(
                "Missing jwt.secret property. Add jwt.secret (Base64) to application.properties"
            );
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(base64Secret);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("jwt.secret must be a valid Base64-encoded string", ex);
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, Collection<String> roles) 
    {
        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(now.getTime() + accessExpirationMinutes * 60L * 1000L);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("type", "access");

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) 
    {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Claims getAllClaims(String token) 
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) 
    {
        return getClaim(token, Claims::getSubject);
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) 
    {
        return resolver.apply(getAllClaims(token));
    }
}
