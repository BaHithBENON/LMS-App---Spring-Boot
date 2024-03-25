package com.lms.library.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtProvider {
	
	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateToken(Authentication authentication) {
    	
    	String username = authentication.getName();

        var expirationDate = Date.from(LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.UTC));

        return Jwts.builder()
                .subject(username)
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
    
    public String generateRefreshToken(
    		Authentication authentication
	) {
    	Object principal = authentication.getPrincipal();
    	System.out.println("Type of principal: " + principal.getClass().getName());
    	String userDetails = (String) principal;
    	System.out.println("Value of Principal: " + principal.toString());
    	return buildToken(new HashMap<>(), principal.toString(), refreshExpiration);
	}
    
    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration
    ) {
      return Jwts
              .builder()
              .claims(extraClaims)
              .subject(username)
              .issuedAt(new Date(System.currentTimeMillis()))
              .expiration(new Date(System.currentTimeMillis() + expiration))
              .signWith(key, Jwts.SIG.HS256)
              .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String extractUsername(String token) {
	    return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
    	var claims = Jwts.parser().verifyWith(key)
                .build().parseSignedClaims(token).getBody();
        return claims;
    }
    
    private boolean hasClaim(String token, String claimName) {
    	final Claims claims = extractAllClaims(token);
    	return claims.get(claimName) != null;
    }
}
