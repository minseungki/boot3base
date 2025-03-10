package com.example.demo.dto.bbs;

import com.example.demo.dto.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter @Setter
@Alias("bbsSelectPageRequest")
@Schema(description = "게시판 Page 조회 요청 모델")
public class BbsSelectPageRequest extends PageRequest {
	/*
	    // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
		@NotNull(message = "필수값입니다.")
		@Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	 */

    @NotNull(message = "게시판 구분코드는 필수값 입니다.")
    @Schema(description = "BBS_구분_코드\n- 공지사항\n- FAQ\n- QnA", allowableValues = {"NOTICE", "FAQ", "QNA"})
    private String bbsSectionCd;

    @Schema(description = "검색어", example = "제목 및 내용 검색")
    private String searchWord;

}
