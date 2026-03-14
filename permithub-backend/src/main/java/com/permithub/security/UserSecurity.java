package com.permithub.security;

import com.permithub.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
    
    public boolean isCurrentUser(Long userId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        return userDetails.getId().equals(userId);
    }
    
    public boolean hasRole(String role) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        return userDetails.getRoles().stream()
                .anyMatch(r -> r.name().equals(role));
    }
}