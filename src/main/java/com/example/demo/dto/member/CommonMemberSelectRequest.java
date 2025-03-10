package com.example.demo.dto.member;

import com.example.demo.dto.common.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Alias("commonMemberSelectRequest")
public class CommonMemberSelectRequest extends BaseRequest {

    private Long memberSeq;
    private String password;

}

