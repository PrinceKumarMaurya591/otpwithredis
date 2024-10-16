package com.au.hackathon.otp.exception;





public class OTPResendFailedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTPResendFailedException(String message) {
        super(message);
    }
}


