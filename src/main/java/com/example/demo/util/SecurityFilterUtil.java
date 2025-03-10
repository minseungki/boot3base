package com.example.demo.util;

import com.example.demo.dto.common.enumeration.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SecurityFilterUtil {

    public void responseFlush(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> map = new HashMap<>();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        map.put("code", errorCode.getCode());
        map.put("message", errorCode.getMessage());
        map.put("data", null);

        mapper.writeValue(responseStream, map);
        responseStream.flush();
    }

}
