package com.au.hackathon.otp.repo;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.au.hackathon.otp.entity.OTPEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//@Repository
//public class OTPRedisRepository {
//
//	private final RedisTemplate<String, Object> redisTemplate;
//
//	@Autowired
//	public OTPRedisRepository(RedisTemplate<String, Object> redisTemplate) {
//		this.redisTemplate = redisTemplate;
//	}
//
//	public void saveOTP(String key, OTPEntity otpEntity) {
//		redisTemplate.opsForValue().set(key, otpEntity);
//	}
//
//	public OTPEntity getOTP(String key) {
//		// Use the ObjectMapper to convert the LinkedHashMap to OTPEntity
//		Object value = redisTemplate.opsForValue().get(key);
//		if (value != null && value instanceof LinkedHashMap) {
//			// Convert LinkedHashMap to OTPEntity
//			ObjectMapper objectMapper = new ObjectMapper();
//			objectMapper.registerModule(new JavaTimeModule());
//			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//			// Map the LinkedHashMap to OTPEntity
//			return objectMapper.convertValue(value, OTPEntity.class);
//		} else if (value instanceof OTPEntity) {
//			return (OTPEntity) value;
//		}
//		return null;
//	}
//
//	public void deleteOTP(String key) {
//		redisTemplate.delete(key);
//	}
//}










@Repository
public class OTPRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public OTPRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOTP(String key, OTPEntity otpEntity, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, otpEntity, timeoutInSeconds, TimeUnit.SECONDS);
    }

    public OTPEntity getOTP(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && value instanceof LinkedHashMap) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.convertValue(value, OTPEntity.class);
        } else if (value instanceof OTPEntity) {
            return (OTPEntity) value;
        }
        return null;
    }

    public void deleteOTP(String key) {
        redisTemplate.delete(key);
    }

    public Integer getRateLimit(String key) {
        return (Integer) redisTemplate.opsForValue().get(key);
    }

    public void incrementRateLimit(String key) {
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 1, TimeUnit.MINUTES); // Rate limit reset after 1 minute
    }
}