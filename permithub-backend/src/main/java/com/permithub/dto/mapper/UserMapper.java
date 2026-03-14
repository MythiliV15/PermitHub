package com.permithub.dto.mapper;

import com.permithub.dto.response.UserResponse;
import com.permithub.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .roles(user.getRoles())
                .isFirstLogin(user.getIsFirstLogin())
                .lastLoginAt(user.getLastLoginAt())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}