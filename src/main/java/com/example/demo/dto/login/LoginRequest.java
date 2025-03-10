package com.example.demo.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.demo.dto.common.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.ibatis.type.Alias;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Alias("loginRequest")
@Schema(description = "로그인 요청 모델")
public class LoginRequest extends BaseRequest {

	/*
        // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
        @NotNull(message = "필수값입니다.")
        @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    */

	@NotNull(message = "ID는 필수값입니다.")
	@Schema(description = "ID", example = "USER")
	private String id;

	@NotNull(message = "비밀번호는 필수값입니다.")
	@Schema(description = "비밀번호", example = "PWD")
	private String password;

	@Schema(description = "회원일련번호", hidden = true)
	private Long memberSeq;

	@Schema(description = "DI", hidden = true)
	private String di;

	@Schema(description = "PASS 암호화 데이터", hidden = true)
	private String passInfo;

}
