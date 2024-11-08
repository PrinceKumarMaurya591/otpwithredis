package com.au.hackathon.otp.request;

public class OTPRequest {
	private String custRef;
	private String requestId;
	private String channel;
	private String mobile;
	private String otpLength;
	private String otpTimeout;
	private String otpType;

	// Getters and Setters

	public String getCustRef() {
		return custRef;
	}

	public void setCustRef(String custRef) {
		this.custRef = custRef;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOtpLength() {
		return otpLength;
	}

	public void setOtpLength(String otpLength) {
		this.otpLength = otpLength;
	}

	public String getOtpTimeout() {
		return otpTimeout;
	}

	public void setOtpTimeout(String otpTimeout) {
		this.otpTimeout = otpTimeout;
	}

	public String getOtpType() {
		return otpType;
	}

	public void setOtpType(String otpType) {
		this.otpType = otpType;
	}

	public OTPRequest(String custRef, String requestId, String channel, String mobile, String otpLength,
			String otpTimeout, String otpType) {
		super();
		this.custRef = custRef;
		this.requestId = requestId;
		this.channel = channel;
		this.mobile = mobile;
		this.otpLength = otpLength;
		this.otpTimeout = otpTimeout;
		this.otpType = otpType;
	}

	public OTPRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters, setters, constructors, etc.

}
