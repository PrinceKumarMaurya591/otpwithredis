package com.au.hackathon.otp.request;

public class ValidateOTPRequestBody {
	
	private String channel;
	    private String custRef;
private String otp;
public String getCustRef() {
	return custRef;
}
public void setCustRef(String custRef) {
	this.custRef = custRef;
}
public String getOtp() {
	return otp;
}
public void setOtp(String otp) {
	this.otp = otp;
}
public ValidateOTPRequestBody(String custRef, String otp, String channel) {
	super();
	this.channel=channel;
	this.custRef = custRef;
	this.otp = otp;
}
public ValidateOTPRequestBody() {
	super();
	// TODO Auto-generated constructor stub
}
public String getChannel() {
	return channel;
}
public void setChannel(String channel) {
	this.channel = channel;
}


}
