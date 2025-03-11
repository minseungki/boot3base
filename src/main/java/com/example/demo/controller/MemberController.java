package com.example.demo.controller;

import com.example.demo.annotation.ApiResponseExample;
import com.example.demo.annotation.ApiResponseExamples;
import com.example.demo.config.TagsConfig;
import com.example.demo.dto.login.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.common.ResponseModel;
import com.example.demo.dto.member.*;
import com.example.demo.service.MemberService;
import com.example.demo.util.RestUtil;

@RestController
@RequiredArgsConstructor
@Tag(name = TagsConfig.TAG_LOGIN_01)
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService service;

	@PostMapping
	@Operation(summary = "회원 가입")
	@ApiResponseExamples(examples = {
			@ApiResponseExample(errorCode = "ERR_MEMBER_001"),
			@ApiResponseExample(errorCode = "ERR_MEMBER_002")
	})
	public ResponseEntity<ResponseModel<LoginResponse>> insert(@Valid @RequestBody MemberInsertRequest req) {
		return RestUtil.ok(service.insert(req));
	}

}
