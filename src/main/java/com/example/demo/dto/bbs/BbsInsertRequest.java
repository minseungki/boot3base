package com.example.demo.dto.bbs;

import com.example.demo.dto.common.BaseRequest;
import com.example.demo.dto.common.SwaggerSample;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter @Setter
@Alias("bbsInsertRequest")
@Schema(description = "게시판 등록 요청 모델")
public class BbsInsertRequest extends BaseRequest {
	/*
        // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
        @NotNull(message = "필수값입니다.")
        @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    */

//    @Schema(description = "파일 목록")
//    private List<FileUploadDto> fileList;

	@Schema(description = "BBS_시퀀스", hidden = true)
	private Long bbsSeq;

	@NotNull(message = "게시판 구분코드는 필수값입니다.")
	@Schema(description = "BBS_구분_코드\n- 공지사항\n- FAQ\n- QnA", allowableValues = {"NOTICE", "FAQ", "QNA"})
	private String bbsSectionCd;

	@NotNull(message = "게시판 제목은 필수값입니다.")
	@Schema(description = "제목", example = "[공지] 신규 업데이트 항목 안내")
	private String title;

	@Schema(description = "내용", example = "1. 약관 제 1조 항목 수정")
	private String contents;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@NotNull(message = "노출_시작_일시는 필수값입니다.")
	@Schema(description = "노출_시작_일시", example = SwaggerSample.LOCAL_DATE_TIME_SAMPLE)
	private LocalDateTime exposureBeginDt;

	@Schema(description = "노출_시작_일시", hidden = true)
	private String queryExposureBeginDt;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@NotNull(message = "노출_종료_일시는 필수값입니다.")
	@Schema(description = "노출_종료_일시", example = SwaggerSample.LOCAL_DATE_TIME_SAMPLE)
	private LocalDateTime exposureEndDt;

	@Schema(description = "노출_종료_일시", hidden = true)
	private String queryExposureEndDt;

	@Schema(description = "노출_여부")
	private Boolean exposureYn;
	
}
