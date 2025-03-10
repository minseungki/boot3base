package com.example.demo.dto.bbs;

import com.example.demo.dto.common.BaseRequest;
import com.example.demo.dto.common.SwaggerSample;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter @Setter
@Alias("bbsDeleteRequest")
@Schema(description = "게시판 삭제 요청 모델")
public class BbsDeleteRequest extends BaseRequest {

	/*
        // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
        @NotNull(message = "필수값입니다.")
        @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    */

	@Schema(description = "BBS_시퀀스", example = SwaggerSample.SEQUENCE_SAMPLE)
	private Long bbsSeq;

}
