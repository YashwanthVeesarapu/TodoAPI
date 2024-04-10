package us.redsols.todo.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    public String generateToken(String username, String uid) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("uid", uid)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Method to extract UID from a JWT
    public String extractUid(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Retrieve the UID claim
        return (String) claims.get("uid");
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.getSubject();
    }

    public boolean validateToken(String authToken) {

        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            return false;
        }

        // try {
        // Claims claims =
        // Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody();
        // return true;
        // } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
        // UnsupportedJwtException
        // | IllegalArgumentException ex) {
        // return false;
        // }
    }
}
