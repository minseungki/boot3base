package com.example.demo.dto.member;

import com.example.demo.dto.common.BaseRequest;
import com.example.demo.dto.common.enumeration.MemberStatusCd;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.ibatis.type.Alias;

@Getter @Setter
@Alias("memberInsertRequest")
@Schema(description = "회원 가입 요청 모델")
public class MemberInsertRequest extends BaseRequest {

	/*
        // required = true 처리를 Validation Annotaion(@NotNull)으로 처리 한다.
        @NotNull(message = "필수값입니다.")
        @Schema(description = "사용자 이름", example = "홍길동", allowableValues = {"홍길동", "김수로"}, hidden = true)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    */

	@Schema(description = "회원_시퀀스", hidden = true)
	private Long memberSeq;

	@NotNull(message = "ID 필수값입니다.")
	@Schema(description = "ID", example = "USER")
	private String id;

	@NotNull(message = "비밀번호 필수값입니다.")
	@Schema(description = "비밀번호", example = "qwer1234Q!@")
	private String password;

	@NotNull(message = "이메일 필수값입니다.")
	@Schema(description = "이메일", example = "test@test.com")
	private String email;

	@Schema(description = "이름", example = "김회원")
	private String name;

	@Schema(description = "전화번호", example = "01000000000")
	private String mobilePhoneNo;

	@Schema(description = "회원_상태_코드", hidden = true)
	private MemberStatusCd memberStatusCd;

}
