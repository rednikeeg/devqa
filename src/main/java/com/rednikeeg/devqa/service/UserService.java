package com.rednikeeg.devqa.service;

import com.rednikeeg.devqa.dto.LoginDto;
import com.rednikeeg.devqa.dto.NewPasswordDto;
import com.rednikeeg.devqa.dto.PasswordResetDto;
import com.rednikeeg.devqa.dto.UserRegistrationDto;
import com.rednikeeg.devqa.entity.User;
import com.rednikeeg.devqa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    
    public User registerUser(UserRegistrationDto registrationDto) {
        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordService.encodePassword(registrationDto.getPassword()))
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .build();
        
        return userRepository.save(user);
    }
    
    public User loginUser(LoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username/email or password");
        }
        
        User user = userOpt.get();
        
        if (!passwordService.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username/email or password");
        }
        
        return user;
    }
    
    public void initiatePasswordReset(PasswordResetDto resetDto) {
        Optional<User> userOpt = userRepository.findByEmail(resetDto.getEmail());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // Token expires in 24 hours
            
            userRepository.save(user);
            
            // In a real application, we would send an email here
            // For this demo, we'll just print the token to console
            System.out.println("Password reset token for " + user.getEmail() + ": " + resetToken);
        }
    }
    
    public void resetPassword(NewPasswordDto newPasswordDto) {
        Optional<User> userOpt = userRepository.findByResetToken(newPasswordDto.getToken());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired reset token");
        }
        
        User user = userOpt.get();
        
        // Check if token is expired
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        // Validate password confirmation
        if (!newPasswordDto.getNewPassword().equals(newPasswordDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Update password and clear reset token
        user.setPassword(passwordService.encodePassword(newPasswordDto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
} 