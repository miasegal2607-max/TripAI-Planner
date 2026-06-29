package com.tripai.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
@Component
public class JwtUtil {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration-ms}") private long expirationMs;
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(secret.getBytes()); }
    public String generateToken(UserDetails u) {
        return Jwts.builder().subject(u.getUsername()).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expirationMs))
                .signWith(getSigningKey()).compact();
    }
    public String extractUsername(String t) { return parseClaims(t).getSubject(); }
    public boolean isTokenValid(String t, UserDetails u) {
        try { return extractUsername(t).equals(u.getUsername()) && !parseClaims(t).getExpiration().before(new Date()); }
        catch(JwtException e) { return false; }
    }
    private Claims parseClaims(String t) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(t).getPayload();
    }
}
