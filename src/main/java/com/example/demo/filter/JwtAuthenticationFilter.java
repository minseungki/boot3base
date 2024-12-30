package com.example.demo.filter;

import com.example.demo.dto.common.enumeration.ErrorCode;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtService.resolveToken(request);

        try {
            if (token != null) {
                if (jwtService.isTokenValid(request, token)) {
                    Authentication auth = jwtService.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (UserNotFoundException ex) {
            request.setAttribute("ERROR_CODE", ErrorCode.ERR401_002);
        }

        filterChain.doFilter(request, response);
    }

}
