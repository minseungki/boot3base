package com.example.demo.controller;

import com.example.demo.dto.common.EmptyResponse;
import com.example.demo.dto.common.ResponseModel;
import com.example.demo.dto.test.TestRequestDto;
import com.example.demo.dto.test.TestResponseDto;
import com.example.demo.util.RestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(
            summary = "이름 저장",
            description = "사용자 이름을 저장합니다.",
            tags = { "02_ㅎ" }
    )
    @PostMapping("/name")
    public ResponseEntity<ResponseModel<EmptyResponse>> saveName(@Valid @RequestBody TestRequestDto req) {
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("이름 : ".concat(req.getName()));
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        return RestUtil.ok();
    }

    @Operation(
            summary = "이름 저장",
            description = "사용자 이름을 저장합니다.",
            tags = { "User" }
    )
    @GetMapping("/name")
    public ResponseEntity<ResponseModel<TestRequestDto>> checkName(@Valid @RequestBody TestRequestDto req) {
        return RestUtil.ok(TestResponseDto.builder().name("이름 : ".concat(req.getName())).build());
    }
}
