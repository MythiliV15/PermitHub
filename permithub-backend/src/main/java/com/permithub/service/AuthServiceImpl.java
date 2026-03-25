package com.permithub.service;

import com.permithub.dto.request.ForgotPasswordRequest;
import com.permithub.dto.request.LoginRequest;
import com.permithub.dto.request.ResetPasswordRequest;
import com.permithub.dto.response.LoginResponse;
import com.permithub.entity.PasswordResetToken;
import com.permithub.entity.User;
import com.permithub.entity.FacultyProfile;
import com.permithub.entity.StudentProfile;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.exception.UnauthorizedException;
import com.permithub.repository.PasswordResetTokenRepository;
import com.permithub.repository.UserRepository;
import com.permithub.repository.FacultyProfileRepository;
import com.permithub.repository.StudentProfileRepository;
import com.permithub.security.CustomUserDetails;
import com.permithub.security.JwtUtil;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetTokenRepository tokenRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        String jwt = jwtUtil.generateToken(user);
        
        // Find full name from profile
        String fullName = "User";
        if ("STUDENT".equals(user.getRole())) {
            fullName = studentProfileRepository.findByUserId(user.getId())
                    .map(StudentProfile::getName).orElse("Student");
        } else {
            fullName = facultyProfileRepository.findByUserId(user.getId())
                    .map(FacultyProfile::getName).orElse("Faculty");
        }
        
        return LoginResponse.builder()
                .token(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(fullName)
                .roles(List.of(user.getRole()))
                .departmentId(user.getDepartmentId())
                .hostelType(user.getHostelType())
                .isFirstLogin(user.getFirstLogin())
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return; // silently return to prevent account enumeration
        }
        User user = userOpt.get();
        tokenRepository.invalidateAllUserTokens(user.getId());
        
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(resetToken);
        
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));
        if (resetToken.isExpired() || resetToken.getUsedAt() != null) {
            throw new BadRequestException("Token has expired or already used");
        }
        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);
        userRepository.save(user);
        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
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
        user.setFirstLogin(false);
        userRepository.save(user);
    }

    @Override
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
        log.info("User logged out and token blacklisted");
    }
}