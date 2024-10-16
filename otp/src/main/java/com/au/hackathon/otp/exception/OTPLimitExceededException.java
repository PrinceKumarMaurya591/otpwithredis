package com.au.hackathon.otp.exception;

public class OTPLimitExceededException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTPLimitExceededException(String message) {
        super(message);
    }
}
