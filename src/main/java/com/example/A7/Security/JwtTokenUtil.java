package com.example.A7.Security;

import com.example.A7.Config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Autowired
    private JwtConfig jwtConfig;

    // 从JWT令牌中提取用户名
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 从JWT令牌中提取过期时间
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 从JWT令牌中提取角色
    public String getRoleFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    // 从JWT令牌中提取指定的声明
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 解析JWT令牌中的所有声明
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查JWT令牌是否过期
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 生成JWT令牌
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // 添加角色信息
        if (userDetails instanceof CustomUserDetails) {
            claims.put("role", ((CustomUserDetails) userDetails).getRole());
            claims.put("userId", ((CustomUserDetails) userDetails).getUserId());
        }
        
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // 创建JWT令牌
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration() * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证JWT令牌
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 获取签名密钥
    private Key getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        // 确保密钥长度足够
        if (keyBytes.length < 32) { // 至少256位（32字节）用于HS256算法
            throw new IllegalArgumentException("JWT密钥长度不足，使用HS256算法需要至少32个字符");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 