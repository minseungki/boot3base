package com.example.demo.dto.bbs;

import com.example.demo.dto.common.SwaggerSample;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter @Setter
@Alias("bbsSelectResponse")
@Schema(description = "게시판 상세 조회 응답 모델")
public class BbsSelectResponse {

    /*
        // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
        @NotNull(message = "필수값입니다.")
        @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    */

    @Schema(description = "BBS_시퀀스", example = SwaggerSample.SEQUENCE_SAMPLE)
    private Long bbsSeq;

    @Schema(description = "제목", example = "[공지] 신규 업데이트 항목 안내")
    private String title;

    @Schema(description = "내용", example = "1. 약관 제 1조 항목 수정")
    private String contents;

    @Schema(description = "노출_시작_일시", example = SwaggerSample.LOCAL_DATE_TIME_SAMPLE)
    private LocalDateTime exposureBeginDt;

    @Schema(description = "노출_종료_일시", example = SwaggerSample.LOCAL_DATE_TIME_SAMPLE)
    private LocalDateTime exposureEndDt;

    @Schema(description = "등록자명", example = "홍길동")
    private String registerName;

    @Schema(description = "등록_일시", example = SwaggerSample.LOCAL_DATE_TIME_SAMPLE)
    private LocalDateTime registrationDt;

    @Schema(description = "수정자명", example = "홍길동")
    private String updaterName;

    @Schema(description = "수정_일시", example = SwaggerSample.LOCAL_DATE_TIME_SAMPLE)
    private LocalDateTime updateDt;

    @Schema(description = "노출_여부")
    private Boolean exposureYn;

//    @Schema(description = "파일 목록")
//    private List<FileUploadDto> fileList;

}

