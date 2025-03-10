package com.example.demo.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "access 토큰 갱신 요청 모델")
public class UpdateAccessTokenRequest {

    @Schema(description = "리프레시 토큰", hidden = true)
    private String refreshToken;

}
