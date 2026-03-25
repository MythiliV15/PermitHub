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
                .role(user.getRole())
                .departmentId(user.getDepartmentId())
                .hostelType(user.getHostelType())
                .isFirstLogin(user.getFirstLogin())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}