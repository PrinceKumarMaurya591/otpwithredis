package com.au.hackathon.otp.exception;

public class OTPValidationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTPValidationException(String message) {
        super(message);
    }
}
