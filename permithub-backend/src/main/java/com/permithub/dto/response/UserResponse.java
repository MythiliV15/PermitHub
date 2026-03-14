package com.permithub.dto.response;

import com.permithub.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String profilePicture;
    private Set<Role> roles;
    private Boolean isFirstLogin;
    private LocalDateTime lastLoginAt;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
}