package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    ERROR("ERR400_001", "오류가 발생했습니다. 관리자에게 문의 하세요.", "미 정의 에러"),
    INVALID_PARAM("ERR400_002", "유효성 검사에서 에러가 발생하였습니다.", "파라미터 에러"),

    ERR401_001("ERR401_001","토큰이 만료되었습니다.", "토큰 만료 에러"),
    ERR401_002("ERR401_002","토큰이 위변조되었습니다.", "토큰 위변조 에러"),
    ERR401_003("ERR401_003","토큰은 필수값입니다.", "토큰 누락 에러"),
    ERR401_999("ERR401_999","토큰 관련 오류가 발생했습니다. 관리자에게 문의 하세요.", "그외 토큰 에러"),

    ERR403_001("ERR403_001","접근이 거부되었습니다.", "권한 부족 에러"),
    ERR404_001("ERR404_001","DATA NOT FOUND", "No Data 에러"),

    SERVER_ERROR("ERR500_001", "장애가 발생했습니다. 관리자에게 문의 하세요.", "서버 내부 오류"),
    SYSTEM_INSPECTION("ERR503_001", "시스템 점검 상태 입니다.", "시스템 점검 오류"),
    ;

    private final String code;
    private final String message;
    private final String desc;

}
