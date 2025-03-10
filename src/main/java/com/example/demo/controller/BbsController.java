package com.example.demo.controller;

import com.example.demo.annotation.ApiResponseExample;
import com.example.demo.annotation.ApiResponseExamples;
import com.example.demo.annotation.SchemaHidden;
import com.example.demo.annotation.SeqParamSchema;
import com.example.demo.config.TagsConfig;
import com.example.demo.dto.bbs.*;
import com.example.demo.dto.common.*;
import com.example.demo.service.BbsService;
import com.example.demo.util.RestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = TagsConfig.TAG_BBS_01)
@RequestMapping("/api/bbs")
public class BbsController {

    private final BbsService service;

    @GetMapping("/code")
    @Operation(summary = "게시판 카테고리 코드 목록 조회")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(errorCode = "ERR_BBS_001")
    })
    public ResponseEntity<ResponseModel<List<BbsCategoryCdSelectListResponse>>> list() {
        return RestUtil.ok(service.list());
    }

    @GetMapping
    @Operation(summary = "게시판 Page 조회")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(errorCode = "ERR_BBS_002")
    })
    public ResponseEntity<ResponseModel<EPageInfo<BbsSelectPageResponse>>> page(@ParameterObject @Valid BbsSelectPageRequest req) {
        return RestUtil.ok(service.page(req));
    }

    @GetMapping("/{bbsSeq}")
    @Operation(summary = "게시판 상세 조회")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(errorCode = "ERR_BBS_003")
    })
    public ResponseEntity<ResponseModel<BbsSelectResponse>> detail(@PathVariable @SeqParamSchema Long bbsSeq, @SchemaHidden BbsSelectRequest req) {
        return RestUtil.ok(service.detail(req));
    }

    @PostMapping
    @Operation(summary = "게시판 등록")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(errorCode = "ERR_BBS_004")
    })
    public ResponseEntity<ResponseModel<EmptyResponse>> insert(@Valid @RequestBody BbsInsertRequest req) {
        service.insert(req);
        return RestUtil.ok();
    }

    @PutMapping
    @Operation(summary = "게시판 수정")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(errorCode = "ERR_BBS_005")
    })
    public ResponseEntity<ResponseModel<EmptyResponse>> update(@Valid @RequestBody BbsUpdateRequest req) throws Exception {
        service.update(req);
        return RestUtil.ok();
    }

    @DeleteMapping("/{bbsSeq}")
    @Operation(summary = "게시판 삭제")
    @ApiResponseExamples(examples = {
            @ApiResponseExample(errorCode = "ERR_BBS_006")
    })
    public ResponseEntity<ResponseModel<EmptyResponse>> delete(@PathVariable @SeqParamSchema Long bbsSeq, @SchemaHidden BbsDeleteRequest req) {
        service.delete(req);
        return RestUtil.ok();
    }

}
