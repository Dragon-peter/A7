package com.example.A7.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret:defaultSecretKey}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private int expiration;
    
    @Value("${jwt.token-prefix:Bearer}")
    private String tokenPrefix;
    
    @Value("${jwt.header:Authorization}")
    private String header;
    
    public String getSecret() {
        return secret;
    }
    
    public int getExpiration() {
        return expiration;
    }
    
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    public String getHeader() {
        return header;
    }
} 