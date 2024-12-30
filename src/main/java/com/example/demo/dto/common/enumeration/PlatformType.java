package com.example.demo.dto.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlatformType {

	front("front"),
	admin("admin"),
	;
	private final String desc;
}
