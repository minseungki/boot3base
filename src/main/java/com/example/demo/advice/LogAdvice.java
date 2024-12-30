package com.example.demo.advice;

import com.example.demo.util.FilterUtil;
import com.example.demo.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAdvice extends FilterUtil {

	private final RequestUtil requestUtil;

	@Pointcut("within(com.example.demo.controller..*)")
	public void onRequest() {
	}

	@Around("com.example.demo.advice.LogAdvice.onRequest()")
	public Object requestLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		long start = System.currentTimeMillis();
		try {
			return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
		} finally {
			//헬스체크, 파일 저장 예외
			if (!"/hc".equals(request.getRequestURI()) && !isFilterWhiteList(request.getServletPath())) {

				long end = System.currentTimeMillis();

				String param = RequestUtil.removeMapInKey(request);
				String body = RequestUtil.removeJsonObjectInKey(request);

				log.debug("Request: {} {}: {} {} ({}ms)", request.getMethod(), request.getRequestURL(), param, body, end - start);

				if (request.getRequestURI().contains("/api")) {
					// todo 액션로그 DB 저장 기능 추가
				}
			}
		}
	}

}