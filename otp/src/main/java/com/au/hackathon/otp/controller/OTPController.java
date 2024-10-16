package com.au.hackathon.otp.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.au.hackathon.otp.exception.OTPChannelMismatchException;
import com.au.hackathon.otp.exception.OTPExpiredException;
import com.au.hackathon.otp.exception.OTPLimitExceededException;
import com.au.hackathon.otp.exception.OTPNotFoundException;
import com.au.hackathon.otp.exception.OTPResendFailedException;
import com.au.hackathon.otp.request.OTPRequest;
import com.au.hackathon.otp.request.OTPResponse;
import com.au.hackathon.otp.request.ValidateOTPRequest;
import com.au.hackathon.otp.request.ValidateOTPRequestBody;
import com.au.hackathon.otp.service.OTPService;

@RestController
@RequestMapping("/api/otp")
public class OTPController {

	private static final Logger log = LoggerFactory.getLogger(OTPController.class);

	private final OTPService otpService;

	@Autowired
	public OTPController(OTPService otpService) {
		this.otpService = otpService;
	}

	@PostMapping("/generate")
	public CompletableFuture<ResponseEntity<OTPResponse>> generateOTP(@RequestBody OTPRequest otpRequest) {
	    OTPResponse otpResponse = new OTPResponse();
	    String custRef = otpRequest.getCustRef();
	    
	    log.info("Received OTP generation request for customer reference: {}", custRef);
	    return CompletableFuture.supplyAsync(()->{
	    try {
	        String plainOtp = otpService.generateOTP(otpRequest);
	        otpResponse.setOtp(plainOtp);
	        otpResponse.setStatusCode("200");
	        otpResponse.setRespMessage("OTP successfully generated.");
	        
	        log.info("OTP generation successful for customer reference: {}", custRef);
	        return ResponseEntity.ok(otpResponse);

	    } catch (OTPLimitExceededException e) {
	        otpResponse.setStatusCode("429");
	        otpResponse.setRespMessage("Rate limit exceeded: " + e.getMessage());
	        log.error("Rate limit exceeded for customer reference: {}", custRef, e);
	        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(otpResponse);

	    } catch (Exception e) {
	        otpResponse.setStatusCode("500");
	        otpResponse.setRespMessage("Internal server error occurred.");
	        log.error("Error generating OTP for customer reference: {}", custRef, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(otpResponse);
	    }
	    });
	}

	@PostMapping("/validate")
	public CompletableFuture<ResponseEntity<OTPResponse>> validateOTP(@RequestBody ValidateOTPRequestBody otpRequest) {
	    OTPResponse otpResponse = new OTPResponse();
	    String custRef = otpRequest.getCustRef();
	    
	    log.info("Received OTP validation request for customer reference: {}", custRef);
	    return CompletableFuture.supplyAsync(()->{
	    try {
	        boolean isValid = otpService.validateOTP(custRef, otpRequest.getOtp(), otpRequest.getChannel());
	        if (isValid) {
	            otpResponse.setStatusCode("200");
	            otpResponse.setRespMessage("OTP successfully validated.");
	            log.info("OTP validation successful for customer reference: {}", custRef);
	            return ResponseEntity.ok(otpResponse);
	        } else {
	            otpResponse.setStatusCode("400");
	            otpResponse.setRespMessage("Invalid OTP.");
	            log.warn("Invalid OTP for customer reference: {}", custRef);
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(otpResponse);
	        }
	    } catch (OTPExpiredException e) {
	        otpResponse.setStatusCode("410");
	        otpResponse.setRespMessage(e.getMessage());
	        log.warn("OTP expired for customer reference: {}", custRef);
	        return ResponseEntity.status(HttpStatus.GONE).body(otpResponse);
	    } catch (OTPNotFoundException e) {
	        otpResponse.setStatusCode("404");
	        otpResponse.setRespMessage(e.getMessage());
	        log.warn("OTP not found for customer reference: {}", custRef);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(otpResponse);
	    } catch (OTPChannelMismatchException e) {
	        otpResponse.setStatusCode("400");
	        otpResponse.setRespMessage(e.getMessage());
	        log.warn("Channel mismatch for OTP validation for customer reference: {}", custRef);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(otpResponse);
	    } catch (Exception e) {
	        otpResponse.setStatusCode("500");
	        otpResponse.setRespMessage("Internal server error.");
	        log.error("Error validating OTP for customer reference: {}", custRef, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(otpResponse);
	    }
	    });
	}

	@PostMapping("/resend")
	public CompletableFuture<ResponseEntity<OTPResponse>> resendOTP(@RequestBody OTPRequest otpRequest) {
	    OTPResponse otpResponse = new OTPResponse();
	    String custRef = otpRequest.getCustRef();
	    
	    log.info("Received OTP resend request for customer reference: {}", custRef);
	    return CompletableFuture.supplyAsync(()->{
	    try {
	        log.info("Attempting to regenerate OTP for customer reference: {}", custRef);
	        String resentOtp = otpService.regenerateOTP(otpRequest);

	        otpResponse.setOtp(resentOtp);
	        otpResponse.setStatusCode("200");
	        otpResponse.setRespMessage("OTP successfully resent.");
	        
	        log.info("OTP successfully resent for customer reference: {}", custRef);
	        return ResponseEntity.ok(otpResponse);

	    } catch (OTPResendFailedException e) {
	        otpResponse.setStatusCode("500");
	        otpResponse.setRespMessage(e.getMessage());
	        log.error("OTP resend failed for customer reference: {}. Error: {}", custRef, e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(otpResponse);

	    } catch (OTPLimitExceededException e) {
	        otpResponse.setStatusCode("429");
	        otpResponse.setRespMessage("Rate limit exceeded: " + e.getMessage());
	        log.error("Rate limit exceeded for customer reference: {}", custRef, e);
	        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(otpResponse);

	    } catch (OTPNotFoundException e) {
	        otpResponse.setStatusCode("404");
	        otpResponse.setRespMessage(e.getMessage());
	        log.warn("OTP not found for customer reference: {}", custRef);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(otpResponse);

	    } catch (Exception e) {
	        otpResponse.setStatusCode("500");
	        otpResponse.setRespMessage("Internal server error.");
	        log.error("Unexpected error while resending OTP for customer reference: {}. Error: {}", custRef, e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(otpResponse);
	    }
	    });
	}

	
	
	

}
