package com.example.demo.filter;

import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.example.demo.config.SecurityConfig.WHITE_LIST;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String HEADER_AUTH;

    @Value("${jwt.prefix}")
    private String HEADER_PREFIX;

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 화이트리스트 URI 토큰 검증 안함
        String requestURI = request.getRequestURI();
        if (Arrays.stream(WHITE_LIST).anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(HEADER_AUTH);

        if (StringUtils.hasText(token) && token.startsWith(HEADER_PREFIX)) {
            token = token.replace(HEADER_PREFIX, "");
            if (jwtUtil.isValidToken(request, token)) {
                Authentication auth = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

}
