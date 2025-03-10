package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatusCd {

    NORMAL("NORMAL"),
    DORMANCY("DORMANCY"),
    SECESSION("SECESSION"),
    ;
    private final String desc;

}

