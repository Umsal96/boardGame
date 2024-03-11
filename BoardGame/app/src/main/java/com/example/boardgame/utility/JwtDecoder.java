package com.example.boardgame.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtDecoder {
    public static String getUserIdFromToken(String jwtToken){
        String secretKey = "g3Zd9Rn$!C7HtP5m@Xw8NqA6fDvSbE1j";
        try{
            // JWT 디코딩
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(jwtToken);

            // 전체 JWT 내용을 JSON 문자열로 변환
            String jwtContent = claimsJws.toString();
            return jwtContent;
        } catch (Exception e){
            e.printStackTrace();;
            return null;
        }
    }
}
