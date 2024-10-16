package com.au.hackathon.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.au.hackathon.otp.exception.OTPResendFailedException;

@Service
public class OTPSenderService {

	private static final Logger log = LoggerFactory.getLogger(OTPSenderService.class);

	private final RestTemplate restTemplate;

	@Value("${axiom.api.url}")
	private String axiomUrl;

	@Value("${axiom.api.key}")
	private String apiKey;

	// Maximum retries before marking OTP send failure
	private static final int MAX_RETRY_ATTEMPTS = 3;

	public OTPSenderService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;

	}

	// Method to send OTP with retry logic
	public boolean sendOTPWithRetry(String mobileNumber, String otp, int retryCount) throws Exception {
		// If retries exceed maximum allowed attempts, throw an exception
		if (retryCount > MAX_RETRY_ATTEMPTS) {
			throw new OTPResendFailedException("Failed to send OTP after maximum retry attempts.");
		}

		boolean isOTPSent = sendOTP(mobileNumber, otp);
		if (!isOTPSent) {
			log.warn("Failed to send OTP, retrying... attempt: {}", retryCount);
			// Retry after a delay (exponential backoff could be added here)
			Thread.sleep(2000 * retryCount); // Sleep for a while before retrying
			return sendOTPWithRetry(mobileNumber, otp, retryCount + 1); // Recursive retry
		}

		return true; // OTP sent successfully
	}

	// Method to send OTP via Axiom SMS API
	public boolean sendOTP(String mobileNumber, String otp) {

		String message = "Your OTP code is: " + otp + ". Please use it to verify your account.";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + apiKey);
		headers.set("Content-Type", "application/json");

		String body = "{\"to\": \"" + mobileNumber + "\", \"message\": \"" + message + "\"}";
		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(axiomUrl, HttpMethod.POST, requestEntity,
					String.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			log.error("Error occurred while sending OTP to Axiom: {}", e.getMessage());
			return false;
		}
	}
}
