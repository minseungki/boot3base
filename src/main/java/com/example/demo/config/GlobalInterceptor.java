package com.example.demo.config;

import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.enumeration.JwtType;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.RestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;


@RequiredArgsConstructor
@Slf4j
public class GlobalInterceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String ip = "127.0.0.1|0:0:0:0:0:0:0:1";

		if ("/swagger-ui.html".equals(request.getRequestURI()) && !(ip.contains(RestUtil.getClientIp()))){
			throw new Exception("접속 금지");
		}

		if (request.getRequestURI().startsWith("/api")) {
			//토큰 사용자 처리
			String token = request.getHeader("Authorization");
			if (StringUtils.hasText(token)) {
				JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(token);
				if (JwtType.access.equals(jwtAuthenticationVo.getJwtType())) {
					log.info("jwt info : {}", jwtAuthenticationVo);
					request.setAttribute("jwtAuthenticationVo", jwtAuthenticationVo);
				}
			}

			// Swagger 접속 유틸 기능 체크 안함
			if (!StringUtils.hasText(request.getHeader("Referer"))
				|| (StringUtils.hasText(request.getHeader("Referer")) && !request.getHeader("Referer").contains("swagger-ui.html"))
			) {
			}
		}


		return true;
	}

}
