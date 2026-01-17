package com.pet.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    private String secretKey = "my-very-secret-key-make-it-long-enough-for-safety-123456789";
    private long expiration = 86400000; // 24小時過期(毫秒)

    //將字串轉成符合安全性規範的加密Key物件
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 1. 產生 Token (印鈔)
    public String createToken(Integer userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 把用戶名存進去
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 從 Token 中解析出用戶名 (讀取證件資訊)
    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    // 3. 驗證 Token 是否有效 (檢查驗鈔)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 這裡可以捕捉不同錯誤：ExpiredJwtException (過期), MalformedJwtException (格式錯誤) 等
            System.out.println("JWT 驗證失敗: " + e.getMessage());
        }
        return false;
    }
}
