package com.au.hackathon.otp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.au.hackathon.otp.request.OTPRequest;
import com.au.hackathon.otp.request.OTPResponse;
import com.au.hackathon.otp.request.OTPStatus;
import com.au.hackathon.otp.request.ValidateOTPRequest;


//@Data
//@Entity
//@Table(name = "otp_details")
//public class OTPEntity implements Serializable{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "cust_ref", nullable = false, length = 50)
//    private String custRef;
//
//    @Column(name = "request_id", nullable = false, length = 50)
//    private String requestId;
//
//    @Column(name = "channel", length = 30)
//    private String channel;
//
//    @Column(name = "mobile", nullable = false, length = 15)
//    private String mobile;
//
//    @Column(name = "otp_length", nullable = false)
//    private int otpLength;
//
//    @Column(name = "otp_timeout", nullable = false)
//    private int otpTimeout;
//
//    @Column(name = "otp_type", length = 20)
//    private String otpType;
//
//    @Column(name = "status_code", length = 10)
//    private String statusCode;
//
//    @Column(name = "otp", nullable = false, length = 10)
//    private String otp;
//
//    @Column(name = "otp_generated_count", nullable = false)
//    private int totalOtpGeneratedCount;
//
//    @Column(name = "otp_validated", nullable = false)
//    private boolean otpValidated;
//
//    @Column(name = "otp_regenerated", nullable = false)
//    private boolean otpRegenerated;
//
//    @Embedded
//    private DefaultEntity defaultEntity;
//
//    // Private constructor used by the Builder
//    private OTPEntity(Builder builder) {
//        this.id = builder.id;
//        this.custRef = builder.custRef;
//        this.requestId = builder.requestId;
//        this.channel = builder.channel;
//        this.mobile = builder.mobile;
//        this.otpLength = builder.otpLength;
//        this.otpTimeout = builder.otpTimeout;
//        this.otpType = builder.otpType;
//        this.statusCode = builder.statusCode;
//        this.otp = builder.otp;
//        this.totalOtpGeneratedCount = builder.totalOtpGeneratedCount;
//        this.otpValidated = builder.otpValidated;
//        this.otpRegenerated = builder.otpRegenerated;
//        this.defaultEntity = builder.defaultEntity;
//    }
//
//    // Static Builder class
//    public static class Builder {
//        private Long id;
//        private String custRef;
//        private String requestId;
//        private String channel;
//        private String mobile;
//        private int otpLength;
//        private int otpTimeout;
//        private String otpType;
//        private String statusCode;
//        private String otp;
//        private int totalOtpGeneratedCount;
//        private boolean otpValidated;
//        private boolean otpRegenerated;
//        private DefaultEntity defaultEntity;
//
//        // Setters for builder pattern
//        public Builder id(Long id) {
//            this.id = id;
//            return this;
//        }
//
//        public Builder custRef(String custRef) {
//            this.custRef = custRef;
//            return this;
//        }
//
//        public Builder requestId(String requestId) {
//            this.requestId = requestId;
//            return this;
//        }
//
//        public Builder channel(String channel) {
//            this.channel = channel;
//            return this;
//        }
//
//        public Builder mobile(String mobile) {
//            this.mobile = mobile;
//            return this;
//        }
//
//        public Builder otpLength(int otpLength) {
//            this.otpLength = otpLength;
//            return this;
//        }
//
//        public Builder otpTimeout(int otpTimeout) {
//            this.otpTimeout = otpTimeout;
//            return this;
//        }
//
//        public Builder otpType(String otpType) {
//            this.otpType = otpType;
//            return this;
//        }
//
//        public Builder statusCode(String statusCode) {
//            this.statusCode = statusCode;
//            return this;
//        }
//
//        public Builder otp(String otp) {
//            this.otp = otp;
//            return this;
//        }
//
//        public Builder totalOtpGeneratedCount(int totalOtpGeneratedCount) {
//            this.totalOtpGeneratedCount = totalOtpGeneratedCount;
//            return this;
//        }
//
//        public Builder otpValidated(boolean otpValidated) {
//            this.otpValidated = otpValidated;
//            return this;
//        }
//
//        public Builder otpRegenerated(boolean otpRegenerated) {
//            this.otpRegenerated = otpRegenerated;
//            return this;
//        }
//
//        public Builder defaultEntity(DefaultEntity defaultEntity) {
//            this.defaultEntity = defaultEntity;
//            return this;
//        }
//
//        // Build method to create an instance of OTPEntity
//        public OTPEntity build() {
//            return new OTPEntity(this);
//        }
//    }
//
//    // Factory methods to build OTPEntity from different request types
//    public static OTPEntity buildFromRequest(OTPRequest otpRequest, OTPResponse otpResponse) {
//        return new Builder()
//                .custRef(otpRequest.getCustRef())
//                .requestId(otpRequest.getRequestId())
//                .channel(otpRequest.getChannel())
//                .mobile(otpRequest.getMobile())
//                .otpLength(otpRequest.getOtpLength())
//                .otpTimeout(otpRequest.getOtpTimeout())
//                .otpType(otpRequest.getOtpType())
//                .otp(otpResponse.getOtp())
//                .statusCode(otpResponse.getStatusCode())
//                .totalOtpGeneratedCount(OTPStatus.SUCCESS.getStatusCode().equals(otpResponse.getStatusCode()) ? 1 : 0)
//                .otpValidated(false)
//                .otpRegenerated(false)
//                .defaultEntity(new DefaultEntity())
//                .build();
//    }
//
//    public static OTPEntity buildFromValidationRequest(ValidateOTPRequest validateOTPRequest, OTPResponse otpResponse) {
//        return new Builder()
//                .custRef(validateOTPRequest.getCustRef())
//                .requestId(validateOTPRequest.getRequestId())
//                .channel(validateOTPRequest.getChannel())
//                .mobile(validateOTPRequest.getMobile())
//                .otpLength(validateOTPRequest.getOtpLength())
//                .otpTimeout(validateOTPRequest.getOtpTimeout())
//                .otpType(validateOTPRequest.getOtpType())
//                .statusCode(otpResponse.getStatusCode())
//                .otpValidated(OTPStatus.SUCCESS.getStatusCode().equals(otpResponse.getStatusCode()))
//                .defaultEntity(new DefaultEntity())
//                .build();
//    }
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getCustRef() {
//		return custRef;
//	}
//
//	public void setCustRef(String custRef) {
//		this.custRef = custRef;
//	}
//
//	public String getRequestId() {
//		return requestId;
//	}
//
//	public void setRequestId(String requestId) {
//		this.requestId = requestId;
//	}
//
//	public String getChannel() {
//		return channel;
//	}
//
//	public void setChannel(String channel) {
//		this.channel = channel;
//	}
//
//	public String getMobile() {
//		return mobile;
//	}
//
//	public void setMobile(String mobile) {
//		this.mobile = mobile;
//	}
//
//	public int getOtpLength() {
//		return otpLength;
//	}
//
//	public void setOtpLength(int otpLength) {
//		this.otpLength = otpLength;
//	}
//
//	public int getOtpTimeout() {
//		return otpTimeout;
//	}
//
//	public void setOtpTimeout(int otpTimeout) {
//		this.otpTimeout = otpTimeout;
//	}
//
//	public String getOtpType() {
//		return otpType;
//	}
//
//	public void setOtpType(String otpType) {
//		this.otpType = otpType;
//	}
//
//	public String getStatusCode() {
//		return statusCode;
//	}
//
//	public void setStatusCode(String statusCode) {
//		this.statusCode = statusCode;
//	}
//
//	public String getOtp() {
//		return otp;
//	}
//
//	public void setOtp(String otp) {
//		this.otp = otp;
//	}
//
//	public int getTotalOtpGeneratedCount() {
//		return totalOtpGeneratedCount;
//	}
//
//	public void setTotalOtpGeneratedCount(int totalOtpGeneratedCount) {
//		this.totalOtpGeneratedCount = totalOtpGeneratedCount;
//	}
//
//	public boolean isOtpValidated() {
//		return otpValidated;
//	}
//
//	public void setOtpValidated(boolean otpValidated) {
//		this.otpValidated = otpValidated;
//	}
//
//	public boolean isOtpRegenerated() {
//		return otpRegenerated;
//	}
//
//	public void setOtpRegenerated(boolean otpRegenerated) {
//		this.otpRegenerated = otpRegenerated;
//	}
//
//	public DefaultEntity getDefaultEntity() {
//		return defaultEntity;
//	}
//
//	public void setDefaultEntity(DefaultEntity defaultEntity) {
//		this.defaultEntity = defaultEntity;
//	}
//
//	public OTPEntity(Long id, String custRef, String requestId, String channel, String mobile, int otpLength,
//			int otpTimeout, String otpType, String statusCode, String otp, int totalOtpGeneratedCount,
//			boolean otpValidated, boolean otpRegenerated, DefaultEntity defaultEntity) {
//		super();
//		this.id = id;
//		this.custRef = custRef;
//		this.requestId = requestId;
//		this.channel = channel;
//		this.mobile = mobile;
//		this.otpLength = otpLength;
//		this.otpTimeout = otpTimeout;
//		this.otpType = otpType;
//		this.statusCode = statusCode;
//		this.otp = otp;
//		this.totalOtpGeneratedCount = totalOtpGeneratedCount;
//		this.otpValidated = otpValidated;
//		this.otpRegenerated = otpRegenerated;
//		this.defaultEntity = defaultEntity;
//	}
//
//	public OTPEntity() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//    // Getters and Setters (Optional if you need them)
//    // You can generate these using your IDE or add them manually
//    
//    
//}



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
