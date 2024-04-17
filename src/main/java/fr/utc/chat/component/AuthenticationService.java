package fr.utc.chat.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthenticationService {
    private final SecretKey secretKey; // 密钥定义为成员变量
    public AuthenticationService() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 在构造函数中生成密钥
    }
    private long expirationTime = 864_000_000; // 设置过期时间，这里为10天
    private String header = "token";

    public String generateToken(String subject,String sessionId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuedAt(now)
                .claim("sessionId",sessionId)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenExpired (Date expirationTime) {
        return expirationTime.before(new Date());
    }
    public Claims getTokenClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getTokenClaim(token).getSubject());
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}

