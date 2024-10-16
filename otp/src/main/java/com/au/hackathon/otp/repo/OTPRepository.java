package com.au.hackathon.otp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.au.hackathon.otp.entity.OTPEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends JpaRepository<OTPEntity, Long> {
    // Additional query methods can be defined here if needed
    OTPEntity findByCustRef(String custRef);
}

