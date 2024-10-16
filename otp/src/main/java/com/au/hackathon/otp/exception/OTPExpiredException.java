package com.au.hackathon.otp.exception;

public class OTPExpiredException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTPExpiredException(String message) {
        super(message);
    }
}
