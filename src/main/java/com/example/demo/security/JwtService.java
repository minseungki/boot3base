package com.example.demo.security;

import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.enumeration.*;
import com.example.demo.util.*;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.header}")
    private String HEADER_AUTH;

    @Value("${jwt.prefix}")
    private String HEADER_PREFIX;

    @Value("${spring.profiles.active}")
    private String profile;

    private final Aes256Util aes256Util;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    public String resolveToken(HttpServletRequest req) {
        String token = req.getHeader(HEADER_AUTH);
        if (StringUtils.hasText(token)) {
            token = token.replace(HEADER_PREFIX, "");
        }
        return token;
    }

    public boolean isTokenValid(HttpServletRequest req, String token) {
        try {
            JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(token);

            String platformType = aes256Util.decrypt(jwtUtil.extractClaim(token, Claims::getSubject));

            if (
                    !PlatformType.admin.getDesc().equals(platformType)
                            || !JwtType.access.equals(jwtAuthenticationVo.getJwtType())
                            || !profile.equals(jwtAuthenticationVo.getProfile().getDesc())
            ) {
                throw new UnsupportedJwtException("Unsupported Type or Profile");
            }
        } catch (ExpiredJwtException ex) {
            // 토큰 만료
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_001);
            return false;
        } catch (JwtException | IllegalArgumentException  ex) {
            // 토큰 위변조
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_002);
            return false;
        } catch (Exception ex) {
            // 토큰 에러
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_999);
            return false;
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(token);
        String userId = jwtAuthenticationVo.getId();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}

