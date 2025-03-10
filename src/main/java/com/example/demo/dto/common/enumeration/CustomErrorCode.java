package com.example.demo.dto.common.enumeration;

public interface CustomErrorCode {
    String getDesc();

    // getCode를 기본적으로 enum의 name()을 사용하도록 강제
    default String getCode() {
        if (this instanceof Enum) {
            return ((Enum<?>) this).name();  // enum 타입일 때 name() 반환
        } else {
            return null;
        }
    }
}
