package com.example.demo.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TestRequestDto {

//    @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"})
    @NotNull(message = "이름은 필수값입니다.")
    private String name;

    @Schema(hidden = true)
    private String title;
}
