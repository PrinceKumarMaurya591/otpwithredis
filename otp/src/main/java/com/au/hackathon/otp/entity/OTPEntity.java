package com.au.hackathon.otp.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cust_ref")
	private String custRef;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "channel")
	private String channel;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "otp_length")
	private String otpLength;

	@Column(name = "otp_timeout")
	private String otpTimeout;

	@Column(name = "otp_type")
	private String otpType;

	@Column(name = "otp_status_code")
	private String statusCode;

	@Column(name = "otp")
	private String otp;

	@Column(name = "otp_generated_count")
	private int totalOtpGeneratedCount;

	@Column(name = "otp_validated")
	private boolean otpValidated;

	@Column(name = "otp_regenerated")
	private boolean otpRegenerated;

	@Embedded
	private DefaultEntity defaultEntity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public int getTotalOtpGeneratedCount() {
		return totalOtpGeneratedCount;
	}

	public void setTotalOtpGeneratedCount(int totalOtpGeneratedCount) {
		this.totalOtpGeneratedCount = totalOtpGeneratedCount;
	}

	public boolean isOtpValidated() {
		return otpValidated;
	}

	public void setOtpValidated(boolean otpValidated) {
		this.otpValidated = otpValidated;
	}

	public boolean isOtpRegenerated() {
		return otpRegenerated;
	}

	public void setOtpRegenerated(boolean otpRegenerated) {
		this.otpRegenerated = otpRegenerated;
	}

	public DefaultEntity getDefaultEntity() {
		return defaultEntity;
	}

	public void setDefaultEntity(DefaultEntity defaultEntity) {
		this.defaultEntity = defaultEntity;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public OTPEntity(Long id, String custRef, String requestId, String channel, String mobile, String otpLength,
			String otpTimeout, String otpType, String statusCode, String otp, int totalOtpGeneratedCount,
			boolean otpValidated, boolean otpRegenerated, DefaultEntity defaultEntity) {
		super();
		this.id = id;
		this.custRef = custRef;
		this.requestId = requestId;
		this.channel = channel;
		this.mobile = mobile;
		this.otpLength = otpLength;
		this.otpTimeout = otpTimeout;
		this.otpType = otpType;
		this.statusCode = statusCode;
		this.otp = otp;
		this.totalOtpGeneratedCount = totalOtpGeneratedCount;
		this.otpValidated = otpValidated;
		this.otpRegenerated = otpRegenerated;
		this.defaultEntity = defaultEntity;
	}

	public OTPEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
