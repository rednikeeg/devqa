package com.rednikeeg.devqa.controller;

import com.rednikeeg.devqa.dto.LoginDto;
import com.rednikeeg.devqa.dto.NewPasswordDto;
import com.rednikeeg.devqa.dto.PasswordResetDto;
import com.rednikeeg.devqa.dto.UserRegistrationDto;
import com.rednikeeg.devqa.entity.User;
import com.rednikeeg.devqa.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/posts";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDto userRegistrationDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        try {
            User user = userService.registerUser(userRegistrationDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute LoginDto loginDto,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        
        try {
            User user = userService.loginUser(loginDto);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Welcome back, " + user.getUsername() + "!");
            return "redirect:/posts";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("passwordResetDto", new PasswordResetDto());
        return "auth/forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute PasswordResetDto passwordResetDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        
        try {
            userService.initiatePasswordReset(passwordResetDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "If an account with that email exists, a password reset link has been sent. Check the console for the token.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/forgot-password";
        }
    }
    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setToken(token);
        model.addAttribute("newPasswordDto", newPasswordDto);
        return "auth/reset-password";
    }
    
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute NewPasswordDto newPasswordDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }
        
        try {
            userService.resetPassword(newPasswordDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Password has been reset successfully. Please login with your new password.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/reset-password";
        }
    }
} 