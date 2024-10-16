package com.au.hackathon.otp.request;

public enum OTPStatus {
    SUCCESS("200"),
    FAILURE("400");

    private String statusCode;

    OTPStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
