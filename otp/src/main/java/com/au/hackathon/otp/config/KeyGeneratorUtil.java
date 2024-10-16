//package com.au.hackathon.otp.config;
//
//import java.util.Base64;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//
//public class KeyGeneratorUtil {
//    public static void main(String[] args) throws Exception {
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(128); // AES-128
//        SecretKey secretKey = keyGen.generateKey();
//        byte[] keyBytes = secretKey.getEncoded();
//        System.out.println(Base64.getEncoder().encodeToString(keyBytes)); // Print Base64 encoded key
//    }
//}
