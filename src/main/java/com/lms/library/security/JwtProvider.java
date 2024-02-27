package com.lms.library.security;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JwtProvider {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateToken(Authentication authentication) {
    	
    	UserDetails user = (UserDetails) authentication.getPrincipal();

        var expirationDate = Date.from(LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.UTC));

        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public String getLoginFromToken(String token) {
        var payload = Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token).getPayload();
        return payload.getSubject();
    }
}
