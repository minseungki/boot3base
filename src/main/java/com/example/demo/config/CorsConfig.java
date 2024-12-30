package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${jwt.header}")
    private String HEADER_AUTH;

    @Bean
    public CorsFilter corsFilter() {

        List<String> listAllowedOrigin;
        CorsConfiguration config = new CorsConfiguration();

        if (profile.equals("prod")) {            // 운영 서버인 경우
            listAllowedOrigin = Arrays.asList(
                    "*localhost*",
                    "http://localhost:[*]"
            );
            config.setAllowedOriginPatterns(listAllowedOrigin);
        } else {                                  // 로컬/개발 서버 인 경우
            config.addAllowedOriginPattern("*");
        }

        config.setAllowCredentials(true);
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader(HEADER_AUTH);

        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.OPTIONS);
        config.addAllowedMethod(HttpMethod.DELETE);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
