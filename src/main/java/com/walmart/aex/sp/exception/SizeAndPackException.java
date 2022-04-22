package com.walmart.aex.sp.exception;

public class SizeAndPackException extends Exception {

	private static final long serialVersionUID = 1L;

	public SizeAndPackException(String errorMessage) {
        super(errorMessage);
    }

	public SizeAndPackException(String message, Throwable cause) {
		super(message, cause);
	}

}
