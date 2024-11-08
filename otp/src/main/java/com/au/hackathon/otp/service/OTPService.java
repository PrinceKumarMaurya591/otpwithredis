package com.au.hackathon.otp.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.au.hackathon.otp.entity.DefaultEntity;
import com.au.hackathon.otp.entity.OTPEntity;
import com.au.hackathon.otp.exception.OTPChannelMismatchException;
import com.au.hackathon.otp.exception.OTPLimitExceededException;
import com.au.hackathon.otp.exception.OTPNotFoundException;
import com.au.hackathon.otp.repo.OTPRedisRepository;
import com.au.hackathon.otp.repo.OTPRepository;
import com.au.hackathon.otp.request.OTPRequest;

@Service
public class OTPService {

	private static final Logger log = LoggerFactory.getLogger(OTPService.class);
	@Value("${encryption.secretKey}")
	private String base64SecretKey;
	private final OTPRepository otpRepository;
	private final OTPRedisRepository redisRepository;
	private static final int MAX_REGENERATE_LIMIT = 50000;
	private static final int MAX_OTP_REQUESTS = 50000; // per minute
	
	@Autowired
	private WebClient.Builder webClientBuilder; // Inject WebClient.Builder
	

	@Autowired
	public OTPService(OTPRepository otpRepository, OTPRedisRepository redisRepository) {
		this.otpRepository = otpRepository;
		this.redisRepository = redisRepository;
	}

	

	
	
	
	
	public String generateOTP(OTPRequest otpRequest) throws Exception {
	    if (isRateLimitExceeded(otpRequest.getCustRef())) {
	        log.warn("Rate limit exceeded for customer reference: {}", otpRequest.getCustRef());
	        throw new OTPLimitExceededException("Rate limit exceeded for customer reference: " + otpRequest.getCustRef());
	    }

	    log.info("Generating OTP for customer reference: {}", otpRequest.getCustRef());
	    var generatedOTP = generateRandomOTP(otpRequest.getOtpLength());
	    log.debug("Generated OTP: {}", generatedOTP);

	    // Encrypt and hash the OTP for secure storage
	    var encryptedOTP = encrypt(generatedOTP);
	    var hashedOTP = hashOTP(encryptedOTP);

	    var otpEntity = new OTPEntity();
	    otpEntity.setOtp(hashedOTP);
	    otpEntity.setCustRef(otpRequest.getCustRef());
	    otpEntity.setChannel(otpRequest.getChannel());
	    otpEntity.setMobile(otpRequest.getMobile());
	    otpEntity.setOtpLength(otpRequest.getOtpLength());
	    otpEntity.setOtpTimeout(otpRequest.getOtpTimeout());
	    otpEntity.setOtpType(otpRequest.getOtpType());
	    otpEntity.setTotalOtpGeneratedCount(1);

	    // Set default entity details
	    var defaultEntity = new DefaultEntity();
	    defaultEntity.setAction("GENERATE");
	    defaultEntity.setCreatedBy("SYSTEM");
	    defaultEntity.setCreationDateTime(LocalDateTime.now());
	    otpEntity.setDefaultEntity(defaultEntity);

	    // Save OTP to Redis and MySQL
	    long otpTimeout = Long.parseLong(otpRequest.getOtpTimeout());
	    redisRepository.saveOTP(otpRequest.getCustRef(), otpEntity, otpTimeout);
	    otpRepository.save(otpEntity);

	    log.info("OTP saved for customer reference: {}", otpRequest.getCustRef());

	    // Send OTP through mock API
	    var requestBody = Map.of(
	        "mobile", otpRequest.getMobile(),
	        "otp", generatedOTP,
	        "channel", otpRequest.getChannel()
	    );

	    try {
	        String responseStatusCode = sendOtpViaApi(requestBody);
	        otpEntity.setStatusCode(responseStatusCode);
	    } catch (Exception e) {
	        log.error("Error calling mock API: {}", e.getMessage(), e);
	        otpEntity.setStatusCode("ERROR");
	    }

	    otpRepository.save(otpEntity);
	    return generatedOTP;
	}

	private String sendOtpViaApi(Map<String, String> requestBody) throws Exception {
	    var webClient = webClientBuilder.build();
	    return webClient.post()
	        .uri("http://localhost:8080/mock-api/send-otp")
	        .bodyValue(requestBody)
	        .retrieve()
	        .toBodilessEntity()
	        .block()
	        .getStatusCode()
	        .toString();
	}

	public boolean validateOTP(String custRef, String otp, String channel) throws Exception {
	    var otpEntity = Optional.ofNullable(redisRepository.getOTP(custRef))
	        .orElseGet(() -> otpRepository.findByCustRef(custRef));

	    if (otpEntity == null) {
	        throw new OTPNotFoundException("OTP not found for customer reference: " + custRef);
	    }

	    if (!otpEntity.getChannel().equals(channel)) {
	        throw new OTPChannelMismatchException("OTP was not sent via the requested channel: " + channel);
	    }

	    var encryptedInputOtp = encrypt(otp);
	    var hashedInputOtp = hashOTP(encryptedInputOtp).trim();
	    var storedHashedOtp = otpEntity.getOtp().trim();

	    if (storedHashedOtp.equals(hashedInputOtp)) {
	        otpEntity.setOtpValidated(true);
	        otpEntity.getDefaultEntity().setAction("VALIDATE");
	        otpRepository.save(otpEntity);
	        redisRepository.deleteOTP(custRef);
	        log.info("OTP validated for customer reference: {}", custRef);
	        return true;
	    }

	    log.error("OTP validation failed for customer reference: {}", custRef);
	    return false;
	}

	public String regenerateOTP(OTPRequest otpRequest) throws Exception {
	    if (isRateLimitExceeded(otpRequest.getCustRef())) {
	        log.warn("Rate limit exceeded for customer reference: {}", otpRequest.getCustRef());
	        throw new OTPLimitExceededException("Rate limit exceeded for customer reference: " + otpRequest.getCustRef());
	    }

	    var existingOtpEntity = Optional.ofNullable(redisRepository.getOTP(otpRequest.getCustRef()))
	        .orElseGet(() -> otpRepository.findByCustRef(otpRequest.getCustRef()));

	    if (existingOtpEntity == null) {
	        throw new OTPNotFoundException("No OTP found for customer reference: " + otpRequest.getCustRef());
	    }

	    var newGeneratedOTP = generateRandomOTP(otpRequest.getOtpLength());
	    log.debug("Regenerated OTP: {}", newGeneratedOTP);

	    var encryptedNewOTP = encrypt(newGeneratedOTP);
	    var hashedNewOTP = hashOTP(encryptedNewOTP);

	    existingOtpEntity.setOtp(hashedNewOTP);
	    existingOtpEntity.setTotalOtpGeneratedCount(existingOtpEntity.getTotalOtpGeneratedCount() + 1);
	    existingOtpEntity.setOtpRegenerated(true);

	    long otpTimeout = Long.parseLong(otpRequest.getOtpTimeout());
	    redisRepository.saveOTP(otpRequest.getCustRef(), existingOtpEntity, otpTimeout);

	    var requestBody = Map.of(
	        "mobile", otpRequest.getMobile(),
	        "otp", newGeneratedOTP,
	        "channel", otpRequest.getChannel()
	    );

	    try {
	        String responseStatusCode = sendOtpViaApi(requestBody);
	        existingOtpEntity.setStatusCode(responseStatusCode);
	    } catch (Exception e) {
	        log.error("Error calling mock API: {}", e.getMessage(), e);
	        existingOtpEntity.setStatusCode("ERROR");
	    }

	    existingOtpEntity.getDefaultEntity().setAction("REGENERATE");
	    existingOtpEntity.getDefaultEntity().setUpdatedBy("SYSTEM");
	    existingOtpEntity.getDefaultEntity().setUpdatedTime(LocalDateTime.now());

	    otpRepository.save(existingOtpEntity);
	    return newGeneratedOTP;
	}

	
	
	
	
	private boolean isRateLimitExceeded(String custRef) {
	    String key = "rate_limit_" + custRef;
	    Integer requestCount = redisRepository.getRateLimit(key);

	    // Use Optional to avoid null checks and make code more readable
	    if (Optional.ofNullable(requestCount).orElse(0) >= MAX_OTP_REQUESTS) {
	        return true;
	    }

	    // Use Redis atomic increment feature to avoid race conditions and unnecessary fetches
	    redisRepository.incrementRateLimit(key); // Let Redis handle atomic increments
	    
	    return false;
	}

	private String generateRandomOTP(String length) {
	    int otpLength = Integer.parseInt(length);
	    return ThreadLocalRandom.current()
	        .ints(otpLength, 0, 10) // Generate a stream of random digits between 0 and 9
	        .mapToObj(String::valueOf)
	        .collect(Collectors.joining()); // Join them into a single string
	}

	
	private SecretKeySpec getSecretKey() throws Exception {
	    byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
	    return new SecretKeySpec(decodedKey, "AES");
	}
	
	private String encrypt(String otp) throws Exception {
	    SecretKeySpec secretKey = getSecretKey();
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	    
	    byte[] encrypted = cipher.doFinal(otp.getBytes(StandardCharsets.UTF_8)); // Ensure UTF-8 encoding
	    return Base64.getEncoder().encodeToString(encrypted);
	}
	
	private String hashOTP(String otp) {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
	        return Base64.getEncoder().encodeToString(hash);
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Error while hashing OTP", e);
	    }
	}

	
	
	



	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
}
