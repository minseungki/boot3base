package com.example.demo.config;

import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilter(corsConfig.corsFilter())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                              "/api/login"                    // 로그인
                            , "/api/access-token"           // 엑세스토큰 갱신
                            , "/api/member"                 // 회원 가입
                            , "/api/member/check/id"        // 회원 가입시 아이디 중복체크
                            , "/api/update-access-token"    // access 토큰 갱신
                            , "/v3/api-docs/**"             // OpenAPI 명세
                            , "/swagger-ui/**"              // Swagger UI 리소스
                            , "/swagger-ui.html"            // Swagger UI 메인 경로
                            ).permitAll()                              // 해당 경로는 인증 없이 허용
                    .anyRequest().authenticated()              // 나머지는 인증 필요
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
        ;
        return http.build();
    }

}
