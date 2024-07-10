package com.surgee.stage_2.services;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.surgee.stage_2.model.User;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Dotenv dotenv;
    public boolean isTokenValid(String token, UserDetails user) {
        String userEmail = extractEmail(token);
        return userEmail.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public String generateToken(User user) {
        String token = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 *60*60*1000))
                .signWith(getSignInKey())
                .compact();
                
        return token;
    }
    
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    } 

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignInKey())
        .build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(dotenv.get("JWT_SECRET_KEY"));
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //Implement later
    public boolean verifyToken(String token) {
        return true;
    }

}
