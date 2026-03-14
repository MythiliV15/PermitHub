package com.permithub.service;

import com.permithub.dto.request.ForgotPasswordRequest;
import com.permithub.dto.request.LoginRequest;
import com.permithub.dto.request.ResetPasswordRequest;
import com.permithub.dto.response.LoginResponse;

public interface AuthService {
    
    LoginResponse authenticateUser(LoginRequest loginRequest, String ipAddress);
    
    void forgotPassword(ForgotPasswordRequest request);
    
    void resetPassword(ResetPasswordRequest request);
    
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    void logout(String token);
}