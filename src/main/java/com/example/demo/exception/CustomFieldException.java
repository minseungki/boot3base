package com.example.demo.exception;

import com.example.demo.dto.common.enumeration.ErrorField;
import lombok.*;

@Getter @Setter
public class CustomFieldException extends RuntimeException {

	private ErrorField errorField;
	private String message;

	public CustomFieldException(ErrorField errorField, String message) {
		super(message);
		this.errorField = errorField;
		this.message = message;
	}

}

