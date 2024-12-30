package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtType {
	access("access"),
	refresh("refresh"),
	;
	private final String desc;
}
