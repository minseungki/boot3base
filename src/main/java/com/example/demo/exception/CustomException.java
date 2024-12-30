package com.example.demo.exception;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class CustomException extends RuntimeException {

	private String code;
	private String message;

}

