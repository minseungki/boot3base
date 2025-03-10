package com.example.demo.exception;

public class CustomJwtException extends RuntimeException {
	public CustomJwtException(String message) {
		super(message);
	}

	public CustomJwtException(Throwable ex) {
		super(ex);
	}
}

