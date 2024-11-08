package com.au.hackathon.otp.request;

public class OTPResponse {
	private String otp;
	private String statusCode;
	private String respMessage;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getRespMessage() {
		return respMessage;
	}

	public void setRespMessage(String respMessage) {
		this.respMessage = respMessage;
	}

	public OTPResponse(String otp, String statusCode, String respMessage) {
		super();
		this.otp = otp;
		this.statusCode = statusCode;
		this.respMessage = respMessage;
	}

	public OTPResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters

}
