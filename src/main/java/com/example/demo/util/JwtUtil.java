package com.example.demo.util;

import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.RedisAuthenticationVo;
import com.example.demo.dto.common.enumeration.*;
import com.example.demo.exception.CustomJwtException;
import com.example.demo.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${jwt.header}")
    private String HEADER_AUTH;
    
    @Value("${jwt.header-refresh}")
    private String HEADER_AUTH_REFRESH;

    @Value("${jwt.prefix}")
    private String HEADER_PREFIX;
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.token-validity-in-time.access}")
    private long JWT_ACCESS_EXPIRATION;

    @Value("${jwt.token-validity-in-time.refresh}")
    private long JWT_REFRESH_EXPIRATION;

    @Value("${jwt.refresh-auto-renew-days:0}")
    private int REFRESH_AUTO_RENEW_DAYS;

    @Value("${jwt.platform-type}")
    private String PLATFORM_TYPE;

    private String CLAIMS_KEY = "info";

    private final Aes256Util aes256Util;
    private final RedisUtil redisUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public boolean isValidToken(HttpServletRequest req, String token) {
        try {
            JwtAuthenticationVo jwtAuthenticationVo = getTokenVo(token);
            String platformType = aes256Util.decrypt(extractClaim(token, Claims::getSubject));

            if (!PLATFORM_TYPE.equals(platformType)
                || !JwtType.access.equals(jwtAuthenticationVo.getJwtType())
                || !profile.equals(jwtAuthenticationVo.getProfile().getDesc())
            ) {
                throw new UnsupportedJwtException("Unsupported Type or Profile");
            }
        } catch (ExpiredJwtException ex) {
            // 토큰 만료
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_001);
            req.setAttribute("Exception", ex);
            return false;
        } catch (JwtException | IllegalArgumentException  ex) {
            // 토큰 위변조
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_002);
            req.setAttribute("Exception", ex);
            return false;
        } catch (Exception ex) {
            // 토큰 에러
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_999);
            req.setAttribute("Exception", ex);
            return false;
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        JwtAuthenticationVo jwtAuthenticationVo = getTokenVo(token);
        String userId = jwtAuthenticationVo.getId();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    public String generateToken(JwtAuthenticationVo jwtAuthenticationVo) {
        jwtAuthenticationVo.setProfile(ProfileType.valueOf(profile));

        if (JwtType.access.getDesc().equals(jwtAuthenticationVo.getJwtType().getDesc())) {
            jwtAuthenticationVo.setExpiration(new Date(System.currentTimeMillis() + JWT_ACCESS_EXPIRATION * 1000 * 60));
        } else if (JwtType.refresh.getDesc().equals(jwtAuthenticationVo.getJwtType().getDesc())) {
            jwtAuthenticationVo.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRATION * 1000 * 60));
        }

        Map<String, Object> claims = new HashMap<>() {{
            put(CLAIMS_KEY, aes256Util.encrypt(RestUtil.classToJsonStr(jwtAuthenticationVo)));
        }};

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
            return RestUtil.jsonStrToClass(aes256Util.decrypt((String) extractAllClaims(token).get(CLAIMS_KEY)), JwtAuthenticationVo.class);
        } catch (ExpiredJwtException ex) {
            // 토큰 만료
            throw ex;
        } catch (JwtException | IllegalArgumentException ex) {
            // 토큰 위변조
            throw new CustomJwtException(ex);
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
            result = sdf.format(dt);
        }
        return result;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean checkRefreshExpiration(Date refreshExpiration) {
        if (REFRESH_AUTO_RENEW_DAYS > 0) {
            LocalDate refreshExpirationDate = refreshExpiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate today = LocalDate.now();
            LocalDate thresholdDate = today.plusDays(REFRESH_AUTO_RENEW_DAYS);

            return !refreshExpirationDate.isBefore(today) && !refreshExpirationDate.isAfter(thresholdDate);
        } else {
            return false;
        }
    }

    public void checkValidToken(String accessToken, String refreshToken) {
        JwtAuthenticationVo accessTokenAuthenticationVo = getTokenVo(accessToken);
        JwtAuthenticationVo refreshTokenAuthenticationVo = getTokenVo(refreshToken);

        // 리프레시 토큰 유효성 검사
        if (!PLATFORM_TYPE.equals(refreshTokenAuthenticationVo.getPlatformType().getDesc())
                || !JwtType.refresh.equals(refreshTokenAuthenticationVo.getJwtType())
                || !profile.equals(refreshTokenAuthenticationVo.getProfile().getDesc())) {
            throw new CustomJwtException("Falsified Refresh token"); // 토큰 위변조 처리
        }

        // 엑세스 토큰 / 리프레시 토큰 비교 확인 ( 리프레시 토큰만 탈취 했을 경우 방지 )
        if (!accessTokenAuthenticationVo.getPlatformType().equals(refreshTokenAuthenticationVo.getPlatformType())
                || !accessTokenAuthenticationVo.getProfile().equals(refreshTokenAuthenticationVo.getProfile())) {
            throw new CustomJwtException("Token issuer mismatch"); // 토큰 위변조 처리
        }
    }

    private String getToken(HttpServletRequest request, String key) {
        String token = request.getHeader(key);
        if (StringUtils.hasText(token) && token.startsWith(HEADER_PREFIX)) {
            return token.replace(HEADER_PREFIX, "");
        } else {
            throw new CustomJwtException("Invalid or missing Bearer token");
        }
    }

    public String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getToken(request, HEADER_AUTH);
    }

    public String getRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getToken(request, HEADER_AUTH_REFRESH);
    }

}

