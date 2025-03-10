package com.example.demo.util;

import com.example.demo.dto.common.JwtAuthenticationVo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
public class LoginUtil {

	@Value("${jwt.header}")
	private String HEADER_AUTH_VALUE;

	@Value("${jwt.prefix}")
	private String HEADER_PREFIX_VALUE;

	private static String HEADER_AUTH;
	private static String HEADER_PREFIX;

	@PostConstruct
	public void init() {
		HEADER_AUTH = HEADER_AUTH_VALUE;
		HEADER_PREFIX = HEADER_PREFIX_VALUE;
	}

	public static Long getLoginUserSeq() {
		if (RestUtil.hasRequest()) {
			String token = RestUtil.getHeader(HEADER_AUTH);
			if (StringUtils.hasText(token) && token.startsWith(HEADER_PREFIX)) {
				token = token.replace(HEADER_PREFIX, "");

				JwtUtil jwtUtil = ApplicationContextProvider.getBean(JwtUtil.class);
				JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(token);
				return !ObjectUtils.isEmpty(jwtAuthenticationVo) ? jwtAuthenticationVo.getSeq() : null;
			} else {
				return null;
			}
		} else {
			return 0L;
		}
    }

}
