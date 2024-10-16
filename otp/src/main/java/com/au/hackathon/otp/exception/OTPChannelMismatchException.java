package com.au.hackathon.otp.exception;

public class OTPChannelMismatchException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTPChannelMismatchException(String message) {
        super(message);
    }
}