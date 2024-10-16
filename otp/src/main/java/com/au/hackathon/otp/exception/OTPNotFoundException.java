package com.au.hackathon.otp.exception;

public class OTPNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTPNotFoundException(String message) {
        super(message);
    }
}
