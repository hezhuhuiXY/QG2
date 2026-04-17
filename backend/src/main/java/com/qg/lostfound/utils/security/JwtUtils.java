package com.qg.lostfound.utils.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils implements InitializingBean {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expire-hours}")
    private long expireHours;

    private static String SECRET;
    private static long EXPIRE_MILLS;
    private static SecretKey SECRET_KEY;

    @Override
    public void afterPropertiesSet() {
        SECRET = this.secret;
        EXPIRE_MILLS = expireHours * 60 * 60 * 1000L;
        SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 JWT 登录令牌
    public static String generateToken(Integer userId, String username, Integer role) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + EXPIRE_MILLS);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .subject(username)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(SECRET_KEY)
                .compact();
    }

    // 解析 token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Integer getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Integer.class);
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public static Integer getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }
}