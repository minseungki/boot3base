package com.example.demo.dto.login;

import com.example.demo.dto.common.enumeration.MemberStatusCd;
import com.example.demo.dto.security.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Alias("loginSelectResponse")
@Schema(description = "로그인 조회 응답 모델")
public class LoginSelectResponse {

    private Long memberSeq;
    private String id;
    private String password;
    private String name;

    private String mobilePhoneNo;
    private LocalDateTime passwordChangeDt;
    private MemberStatusCd memberStatusCd;
    private int loginFailureCount;
    private boolean autoLoginYn;

    private Role role;

    private String di;


}
