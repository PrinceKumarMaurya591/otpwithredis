package com.au.hackathon.otp.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock-api")
public class MockApiController {
	private static final Logger log = LoggerFactory.getLogger(MockApiController.class);

	@PostMapping("/send-otp")
	public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
		String mobile = request.get("mobile");
		String otp = request.get("otp");
		String channel = request.get("channel");

		// Log received values for testing
		log.info("Received OTP: {}, Mobile: {}, Channel: {}", otp, mobile, channel);

		// Simulate a successful response
		Map<String, String> response = new HashMap();
		response.put("status", "success");
		response.put("message", "OTP sent successfully.");

		return ResponseEntity.ok(response);
	}
}
