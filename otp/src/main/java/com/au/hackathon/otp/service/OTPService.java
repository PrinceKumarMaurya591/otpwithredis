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

	
	
//	public String generateOTP(OTPRequest otpRequest) throws Exception {
//	    if (isRateLimitExceeded(otpRequest.getCustRef())) {
//	        log.warn("Rate limit exceeded for customer reference: {}", otpRequest.getCustRef());
//	        throw new OTPLimitExceededException("Rate limit exceeded for customer reference: " + otpRequest.getCustRef());
//	    }
//
//	    // Generate a random 6-digit OTP
//	    log.info("Generating OTP for customer reference: {}", otpRequest.getCustRef());
//	    String generatedOTP = generateRandomOTP(otpRequest.getOtpLength());
//	    log.info("Generated OTP: {}", generatedOTP);
//
//	    // Encrypt and Hash the OTP for storage
//	    String encryptedOTP = encrypt(generatedOTP);
//	    String hashedOTP = hashOTP(encryptedOTP);
//
//	    // Map OTPEntity and store the hashed OTP for security
//	    OTPEntity otpEntity = new OTPEntity();
//	    otpEntity.setOtp(hashedOTP);
//	    otpEntity.setCustRef(otpRequest.getCustRef());
//	    otpEntity.setChannel(otpRequest.getChannel());
//	    otpEntity.setMobile(otpRequest.getMobile());
//	    otpEntity.setOtpLength(otpRequest.getOtpLength());
//	    otpEntity.setOtpTimeout(otpRequest.getOtpTimeout());
//	    otpEntity.setOtpType(otpRequest.getOtpType());
//	    otpEntity.setTotalOtpGeneratedCount(1);
//
//	    // Set additional fields
//	    DefaultEntity defaultEntity = new DefaultEntity();
//	    defaultEntity.setAction("GENERATE");
//	    defaultEntity.setCreatedBy("SYSTEM");
//	    defaultEntity.setCreationDateTime(LocalDateTime.now());
//	    otpEntity.setDefaultEntity(defaultEntity);
//
//	    // Save OTP in Redis with TTL
//	    long otpTimeout = Long.parseLong(otpRequest.getOtpTimeout());
//	    redisRepository.saveOTP(otpRequest.getCustRef(), otpEntity, otpTimeout);
//	    log.info("OTP saved in Redis for customer reference: {}", otpRequest.getCustRef());
//
//	    // Save OTP in MySQL
//	    otpRepository.save(otpEntity);
//	    log.info("OTP saved in MySQL for customer reference: {}", otpRequest.getCustRef());
//
//	    // Call the mock API to send OTP
//	    String apiUrl = "http://localhost:8080/mock-api/send-otp"; // Local mock endpoint
//	    Map<String, String> requestBody = new HashMap<>();
//	    requestBody.put("mobile", otpRequest.getMobile());
//	    requestBody.put("otp", generatedOTP);
//	    requestBody.put("channel", otpRequest.getChannel());
//
//	    try {
//	        WebClient webClient = webClientBuilder.build();
//	        WebClient.ResponseSpec responseSpec = webClient.post()
//	            .uri(apiUrl)
//	            .bodyValue(requestBody)
//	            .retrieve();
//
//	        String responseStatusCode = responseSpec.toBodilessEntity().block().getStatusCode().toString();
//	        log.info("Mock API response code: {}", responseStatusCode);
//	        otpEntity.setStatusCode(responseStatusCode); // Save response code in database
//
//	    } catch (Exception e) {
//	        log.error("Error calling mock API: {}", e.getMessage(), e);
//	        otpEntity.setStatusCode("ERROR");
//	    }
//
//	    // Save updated OTP entity with response code
//	    otpRepository.save(otpEntity);
//
//	    // Return the plain 6-digit OTP to the caller (not the hashed/encrypted one)
//	    return generatedOTP;
//	}
//
//	
//	public boolean validateOTP(String custRef, String otp,String channel) throws Exception {
//		OTPEntity otpEntity = redisRepository.getOTP(custRef);
//		if (otpEntity == null) {
//			Optional<OTPEntity> entityFromDb = Optional.ofNullable(otpRepository.findByCustRef(custRef));
//			if (entityFromDb.isPresent()) {
//				otpEntity = entityFromDb.get();
//			} else {
//				throw new OTPNotFoundException("OTP not found for customer reference: " + custRef);
//			}
//		}
//
//		// Ensure the OTP was sent via the correct channel
//	    if (!otpEntity.getChannel().equals(channel)) {
//	        throw new OTPChannelMismatchException("OTP was not sent via the requested channel: " + channel);
//	    }
//		
//		
//		// Encrypt and hash the input OTP
//		String encryptedInputOtp = encrypt(otp); // Encrypt the user-entered OTP
//		String hashedInputOtp = hashOTP(encryptedInputOtp); // Hash the encrypted OTP
//
//		// Trim both the stored and input hashed OTP for comparison
//		hashedInputOtp = hashedInputOtp.trim();
//		String storedHashedOtp = otpEntity.getOtp().trim();
//
//		// Log both values for debugging
//		log.info("Hashed input OTP: {}", hashedInputOtp);
//		log.info("Stored OTP: {}", storedHashedOtp);
//
//		// Log data types to check consistency
//		log.info("Data type of hashed input OTP: {}", hashedInputOtp.getClass().getName());
//		log.info("Data type of stored OTP: {}", storedHashedOtp.getClass().getName());
//
//		// Validate OTP
//		boolean isValid = storedHashedOtp.equals(hashedInputOtp);
//		if (isValid) {
//			otpEntity.setOtpValidated(true);
//			otpEntity.getDefaultEntity().setAction("VALIDATE");
//			otpRepository.save(otpEntity); // Save validated OTP
//			redisRepository.deleteOTP(custRef); // Remove from Redis
//			log.info("OTP successfully validated for customer: {}", custRef);
//		} else {
//			log.error("OTP validation failed for customer: {}", custRef);
//		}
//
//		return isValid; // Return whether OTP validation succeeded
//	}
//
//
//	
//	public String regenerateOTP(OTPRequest otpRequest) throws Exception {
//	    // Check if rate limit is exceeded
//	    if (isRateLimitExceeded(otpRequest.getCustRef())) {
//	        log.warn("Rate limit exceeded for customer reference: {}", otpRequest.getCustRef());
//	        throw new OTPLimitExceededException("Rate limit exceeded for customer reference: " + otpRequest.getCustRef());
//	    }
//
//	    // Fetch the existing OTP Entity
//	    OTPEntity existingOtpEntity = redisRepository.getOTP(otpRequest.getCustRef());
//	    if (existingOtpEntity == null) {
//	        existingOtpEntity = otpRepository.findByCustRef(otpRequest.getCustRef());
//	        if (existingOtpEntity == null) {
//	            throw new OTPNotFoundException("No OTP found for customer reference: " + otpRequest.getCustRef());
//	        }
//	    }
//
//	    // Generate a new OTP
//	    log.info("Regenerating OTP for customer reference: {}", otpRequest.getCustRef());
//	    String newGeneratedOTP = generateRandomOTP(otpRequest.getOtpLength());
//	    log.info("Newly generated OTP: {}", newGeneratedOTP);
//
//	    // Encrypt and Hash the new OTP
//	    String encryptedNewOTP = encrypt(newGeneratedOTP);
//	    String hashedNewOTP = hashOTP(encryptedNewOTP);
//
//	    // Update OTPEntity with new OTP
//	    existingOtpEntity.setOtp(hashedNewOTP);
//	    existingOtpEntity.setTotalOtpGeneratedCount(existingOtpEntity.getTotalOtpGeneratedCount() + 1);
//	    existingOtpEntity.setOtpRegenerated(true); // Mark as regenerated
//
//	    // Save updated OTP in Redis with TTL
//	    long otpTimeout = Long.parseLong(otpRequest.getOtpTimeout());
//	    redisRepository.saveOTP(otpRequest.getCustRef(), existingOtpEntity, otpTimeout);
//
//	    // Call the mock API to send the regenerated OTP
//	    String apiUrl = "http://localhost:8080/mock-api/send-otp"; // Local mock endpoint
//	    Map<String, String> requestBody = new HashMap<>();
//	    requestBody.put("mobile", otpRequest.getMobile());
//	    requestBody.put("otp", newGeneratedOTP);
//	    requestBody.put("channel", otpRequest.getChannel());
//
//	    try {
//	        WebClient webClient = webClientBuilder.build();
//	        WebClient.ResponseSpec responseSpec = webClient.post()
//	            .uri(apiUrl)
//	            .bodyValue(requestBody)
//	            .retrieve();
//
//	        String responseStatusCode = responseSpec.toBodilessEntity().block().getStatusCode().toString();
//	        log.info("Mock API response code: {}", responseStatusCode);
//	        existingOtpEntity.setStatusCode(responseStatusCode); // Save response code in database
//
//	    } catch (Exception e) {
//	        log.error("Error calling mock API: {}", e.getMessage(), e);
//	        existingOtpEntity.setStatusCode("ERROR");
//	    }
//
//	    // Update audit fields
//	    DefaultEntity defaultEntity = existingOtpEntity.getDefaultEntity();
//	    defaultEntity.setAction("REGENRATE");
//	    defaultEntity.setUpdatedBy("SYSTEM");
//	    defaultEntity.setUpdatedTime(LocalDateTime.now());
//
//	    // Save updated OTP entity with response code
//	    otpRepository.save(existingOtpEntity);
//
//	    // Return the new plain OTP
//	    return newGeneratedOTP;
//	}
//	
//	
//	private boolean isRateLimitExceeded(String custRef) {
//	String key = "rate_limit_" + custRef;
//	Integer requestCount = redisRepository.getRateLimit(key);
//	if (requestCount != null && requestCount >= MAX_OTP_REQUESTS) {
//		return true;
//	}
//	// Increment the count for the next request
//	redisRepository.incrementRateLimit(key);
//	return false;
//}
//
//private String generateRandomOTP(String length) {
//	int otpLength = Integer.parseInt(length);
//	Random random = new Random();
//	StringBuilder otp = new StringBuilder();
//	for (int i = 0; i < otpLength; i++) {
//		otp.append(random.nextInt(10)); // Generate digits only
//	}
//	return otp.toString();
//}
//
//private SecretKeySpec getSecretKey() throws Exception {
//	byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
//	return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//}
//
//private String encrypt(String otp) throws Exception {
//	SecretKeySpec secretKey = getSecretKey();
//	Cipher cipher = Cipher.getInstance("AES");
//	cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//	byte[] encrypted = cipher.doFinal(otp.getBytes());
//	return Base64.getEncoder().encodeToString(encrypted);
//}
//
//private String hashOTP(String otp) {
//	try {
//		MessageDigest digest = MessageDigest.getInstance("SHA-256");
//		byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
//		return Base64.getEncoder().encodeToString(hash);
//	} catch (NoSuchAlgorithmException e) {
//		throw new RuntimeException("Error while hashing OTP", e);
//	}
//}
	
	
	
	
	
	
	
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
