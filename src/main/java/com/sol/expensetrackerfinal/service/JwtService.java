package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // ✅ Secure Base64-encoded secret key (generated programmatically)
    // Allowed chars: A–Z, a–z, 0–9, +, /, and =

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(user.getEmail())
                .issuer("EXPENSE_TRACKER_APP")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1 day
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] decode = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValidToken(String jwtToken, UserDetails userDetails) {
        if (jwtToken == null || userDetails == null) {
            return false;
        }
        String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername()) &&
                extractAllClaims(jwtToken).getExpiration().after(new Date()));
    }
}
