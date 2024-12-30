package com.example.demo.security;

import com.example.demo.dto.common.enumeration.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode exception = (ErrorCode)request.getAttribute("ERROR_CODE");

        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> map = new HashMap<>();
        ErrorCode errorCode = null;

        if (ObjectUtils.isEmpty(exception)) {
            errorCode = ErrorCode.ERR401_003;
        } else {
            errorCode = exception;
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        map.put("code", errorCode.getCode());
        map.put("message", errorCode.getMessage());
        map.put("data", null);

        mapper.writeValue(responseStream, map);
        responseStream.flush();
    }

}
