package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    ERROR("ERR400", ""),
    INVALID_PARAM("ERR400_001", "파라미터 오류"),
    ERR401_001("ERR401_001","토큰만료"),
    ERR401_002("ERR401_002","토큰 위변조"),
    ERR401_003("ERR401_003","토큰 필수값"),
    ERR401_999("ERR401_999","토큰 오류 서버 확인 필요"),

    ERR404_001("ERR404_001","FILE NOT FOUND"),
    ERR404_002("ERR404_002","DATA NOT FOUND"),

    SERVER_ERROR("ERR500_001", "장애가 발생했습니다."),
    ;

    private final String code;
    private final String message;

}
