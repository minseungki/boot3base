package com.example.demo.security;

import com.example.demo.dto.common.enumeration.ErrorCode;
import com.example.demo.util.RestUtil;
import com.example.demo.util.SecurityFilterUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint extends SecurityFilterUtil implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode error = (ErrorCode)request.getAttribute("ERROR_CODE");
        Exception ex = (Exception) request.getAttribute("Exception");

        if (!ObjectUtils.isEmpty(ex)) {
            RestUtil.printStackTraceInfo(ex);
        }

        ErrorCode errorCode = null;
        if (ObjectUtils.isEmpty(error)) {
            errorCode = ErrorCode.ERR401_003;
        } else {
            errorCode = error;
        }

        responseFlush(response, errorCode);
    }

}
