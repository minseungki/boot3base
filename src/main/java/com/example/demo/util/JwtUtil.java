package com.example.demo.util;

import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.enumeration.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.token-validity-in-seconds.access}")
    private long JWT_ACCESS_EXPIRATION;

    @Value("${jwt.token-validity-in-seconds.refresh}")
    private long JWT_REFRESH_EXPIRATION;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${jwt.header}")
    private String HEADER_AUTH;

    @Value("${jwt.prefix}")
    private String HEADER_PREFIX;

    private final Map<String, Object> claims = new HashMap<>();
    private final Aes256Util aes256Util;

    public String generateToken(JwtAuthenticationVo jwtAuthenticationVo) {
        jwtAuthenticationVo.setProfile(ProfileType.valueOf(profile));

        if (JwtType.access.getDesc().equals(jwtAuthenticationVo.getJwtType().getDesc())) {
            jwtAuthenticationVo.setExpiration(new Date(System.currentTimeMillis() + JWT_ACCESS_EXPIRATION * 1000 * 60));
        } else if (JwtType.refresh.getDesc().equals(jwtAuthenticationVo.getJwtType().getDesc())) {
            jwtAuthenticationVo.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRATION * 1000 * 60));
        }

        claims.put("info", aes256Util.encrypt(RestUtil.classToJsonStr(jwtAuthenticationVo)));
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(aes256Util.encrypt(jwtAuthenticationVo.getPlatformType().getDesc()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(jwtAuthenticationVo.getExpiration())
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtAuthenticationVo getTokenVo(String token) {
        try {
            return RestUtil.jsonStrToClass(aes256Util.decrypt((String) extractAllClaims(token).get("info")), JwtAuthenticationVo.class);
        } catch (ExpiredJwtException ex) {
            // 토큰 만료
            throw ex;
        } catch (JwtException | IllegalArgumentException ex) {
            // 토큰 위변조
            throw new com.example.demo.exception.JwtException();
        } catch (Exception ex) {
            // 토큰 에러
            throw ex;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String dateToDateFormatString(Date dt) {
        String result = "";
        if (!ObjectUtils.isEmpty(dt)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            result =  sdf.format(dt);
        }
        return result;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

}

