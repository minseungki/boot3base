package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileType {
	local("local"),
	dev("dev"),
	prod("prod"),
	;
	private final String desc;
}
