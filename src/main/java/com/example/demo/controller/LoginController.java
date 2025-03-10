package com.example.demo.controller;

import com.example.demo.annotation.ApiResponseExample;
import com.example.demo.annotation.ApiResponseExamples;
import com.example.demo.annotation.SchemaHidden;
import com.example.demo.config.SpringDocConfig;
import com.example.demo.config.TagsConfig;
import com.example.demo.dto.common.*;
import com.example.demo.dto.login.*;
import com.example.demo.service.LoginService;
import com.example.demo.util.RestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = TagsConfig.TAG_LOGIN_01)
@RequestMapping("/api")
public class LoginController {

    private final LoginService service;
    private final SpringDocConfig springDocConfig;


    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiResponseExamples(examples = {
        @ApiResponseExample(errorCode = "ERR_LOGIN_001")
    })
	public ResponseEntity<ResponseModel<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        return RestUtil.ok(service.login(req));
    }

	@PutMapping("/access-token")
    @Operation(summary = "access 토큰 갱신",
        description = "- # refresh 토큰 만료 15일 전이면 refresh 토큰 새로 발급\n"
                    + "- # Authorization-Refresh 헤더도 Bearer 방식으로 요청"
    )
    @ApiResponseExamples(examples = {
        @ApiResponseExample(errorCode = "ERR_LOGIN_002"),
        @ApiResponseExample(httpStatus = "401", enumName = "ErrorCode", errorCode = "ERR401_002")
    })
	public ResponseEntity<ResponseModel<LoginResponse>> updateAccessToken(
        @SchemaHidden UpdateAccessTokenRequest req,
        @RequestHeader(value = "#{springDocConfig.HEADER_AUTH_REFRESH}") String refreshToken
    ) {
        return RestUtil.ok(service.updateAccessToken(req));
    }

}
