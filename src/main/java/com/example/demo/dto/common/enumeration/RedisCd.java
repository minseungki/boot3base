package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisCd {

	MEM001("회원 정보"),
	;

	private final String desc;
}
