package thminiprojthebook.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secret = "aivle-mini-project-super-secret-key-123456"; // 최소 32자 이상
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());
//    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expiration = 1000 * 60 * 60; // 1시간
    
    public String generateToken(String authorId) {
        return Jwts.builder()
                .setSubject(authorId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String validateAndGetUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}