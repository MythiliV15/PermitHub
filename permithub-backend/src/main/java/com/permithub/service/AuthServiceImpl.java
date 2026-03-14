package com.permithub.service;

import com.permithub.dto.request.ForgotPasswordRequest;
import com.permithub.dto.request.LoginRequest;
import com.permithub.dto.request.ResetPasswordRequest;
import com.permithub.dto.response.LoginResponse;
import com.permithub.entity.PasswordResetToken;
import com.permithub.entity.User;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.exception.UnauthorizedException;
import com.permithub.repository.PasswordResetTokenRepository;
import com.permithub.repository.UserRepository;
import com.permithub.security.CustomUserDetails;
import com.permithub.security.JwtUtil;
import com.permithub.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(authentication);
        
        // Update last login
        userRepository.updateLastLogin(userDetails.getUsername(), LocalDateTime.now(), ipAddress);
        
        return LoginResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .fullName(userDetails.getUser().getFullName())
                .roles(userDetails.getRoles())
                .isFirstLogin(userDetails.getIsFirstLogin())
                .message(userDetails.getIsFirstLogin() ? Constants.MSG_FIRST_LOGIN_PASSWORD_CHANGE : "Login successful")
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        
        // Invalidate any existing tokens
        tokenRepository.invalidateAllUserTokens(user.getId());
        
        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_TOKEN_EXPIRY_MINUTES))
                .isUsed(false)
                .build();
        
        tokenRepository.save(resetToken);
        
        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));
        
        if (resetToken.isExpired() || resetToken.getIsUsed()) {
            throw new BadRequestException("Token has expired or already used");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsFirstLogin(false);
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setIsUsed(true);
        tokenRepository.save(resetToken);
        
        log.info("Password reset successful for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setIsFirstLogin(false);
        userRepository.save(user);
        
        log.info("Password changed for user: {}", user.getEmail());
    }

    @Override
    public void logout(String token) {
        // In a real implementation, you might want to blacklist the token
        // For now, just log the logout
        log.info("User logged out with token: {}", token);
    }
}