package com.example.demo.controller;

import com.example.demo.annotation.ApiResponseExample;
import com.example.demo.annotation.ApiResponseExamples;
import com.example.demo.config.TagsConfig;
import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.ResponseModel;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.RestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequiredArgsConstructor
@Tag(name = TagsConfig.TAG_COM_01)
@RequestMapping("/api/com")
public class CommonController {

    @Value("${jwt.prefix}")
    private String HEADER_PREFIX;

    private final JwtUtil jwtUtil;

    @PostMapping("/decode-token")
    @PreAuthorize("hasRole('DEVELOPER')")
    @Operation(summary = "토큰 복호화", description = "- # Bearer 없이 토큰만 입력")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(httpStatus = "403", enumName = "ErrorCode", errorCode = "ERR403_001")
    })
	public ResponseEntity<ResponseModel<JwtAuthenticationVo>> decodeToken(@RequestBody String token) {
        JwtAuthenticationVo body = new JwtAuthenticationVo();
        if (StringUtils.hasText(token)) {
            token = token.replaceAll("\"", "");
            if (token.startsWith(HEADER_PREFIX)) {
                token = token.replace(HEADER_PREFIX, "");
            }
            body = jwtUtil.getTokenVo(token);
        }

        return RestUtil.ok(body);
    }

}
