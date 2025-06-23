package com.rednikeeg.devqa.service;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class PasswordService {
    
    public String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
    
    public String decodePassword(String encodedPassword) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
        return new String(decodedBytes);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        String decodedPassword = decodePassword(encodedPassword);
        return rawPassword.equals(decodedPassword);
    }
} 