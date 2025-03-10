package com.example.demo.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import com.example.demo.dto.common.SwaggerSample;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Alias("loginResponse")
@Schema(description = "로그인 응답 모델")
public class LoginResponse {

	@Schema(description = "비밀번호 만료 여부 true이면 갱신 팝업 노출", example = "false")
	private boolean passwordExpiredYn;

	@Schema(description = "access 토큰", example = "abc...")
	private String accessToken;

	@Schema(description = "accessToken 토큰만료일자", example = "2022.09.06 09:00.000")
	private String accessTokenExpiredDt;

	@Schema(description = "refresh 토큰", example = "abc...")
	private String refreshToken;

	@Schema(description = "refreshToken 토큰만료일자", example = "2022.09.06 09:00.000")
	private String refreshTokenExpiredDt;

}
