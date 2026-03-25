package com.permithub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String role;
    private Long departmentId;
    private String hostelType;
    private Boolean isFirstLogin;
    private LocalDateTime lastLoginAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
}