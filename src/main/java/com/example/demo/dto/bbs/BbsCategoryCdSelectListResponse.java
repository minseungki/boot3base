package com.example.demo.dto.bbs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("bbsCategoryCdSelectListResponse")
@Schema(description = "게시판 카테고리 코드 목록 조회 응답 모델")
public class BbsCategoryCdSelectListResponse {

    /*
        // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
        @NotNull(message = "필수값입니다.")
        @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    */

    @Schema(description = "코드", example = "NOTICE", allowableValues = {"NOTICE", "FAQ", "QNA"})
    private String code;
    @Schema(description = "라벨", example = "공지사항", allowableValues = {"공지사항", "FAQ", "QnA"})
    private String label;

}
