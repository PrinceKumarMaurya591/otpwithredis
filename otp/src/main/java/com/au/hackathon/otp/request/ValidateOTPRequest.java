package com.au.hackathon.otp.request;

public class ValidateOTPRequest {
    private String custRef;
    private String requestId;
    private String channel;
    private String mobile;
    private int otpLength;
    private int otpTimeout;
    private String otpType;
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
	public int getOtpLength() {
		return otpLength;
	}
	public void setOtpLength(int otpLength) {
		this.otpLength = otpLength;
	}
	public int getOtpTimeout() {
		return otpTimeout;
	}
	public void setOtpTimeout(int otpTimeout) {
		this.otpTimeout = otpTimeout;
	}
	public String getOtpType() {
		return otpType;
	}
	public void setOtpType(String otpType) {
		this.otpType = otpType;
	}
	public ValidateOTPRequest(String custRef, String requestId, String channel, String mobile, int otpLength,
			int otpTimeout, String otpType) {
		super();
		this.custRef = custRef;
		this.requestId = requestId;
		this.channel = channel;
		this.mobile = mobile;
		this.otpLength = otpLength;
		this.otpTimeout = otpTimeout;
		this.otpType = otpType;
	}
	public ValidateOTPRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

   
}
