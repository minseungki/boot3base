package com.example.demo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "응답 기본 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class ResponseModel<T> {

    @Schema(description = "응답 코드", example = "SUC200_001")
    private String code;

    @Schema(description = "응답 메세지", example = "처리가 완료되었습니다.")
    private String message;

    @Schema(description = "응답 객체")
    private T data;

}
