package com.example.demo.exception;

import com.example.demo.dto.common.enumeration.CustomErrorCode;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class CustomException extends RuntimeException {

	private String code;
	private String message;

	public CustomException(CustomErrorCode customErrorCode) {
		super(customErrorCode.getDesc());
		this.code = customErrorCode.getCode();
		this.message = customErrorCode.getDesc();
	}

}

