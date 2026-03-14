package com.permithub.service;

import com.permithub.dto.response.UserResponse;
import com.permithub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    UserResponse getUserById(Long id);
    
    UserResponse getUserByEmail(String email);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    List<UserResponse> getAllActiveUsers();
    
    UserResponse updateUser(Long id, User userDetails);
    
    void deleteUser(Long id);
    
    void deactivateUser(Long id);
    
    void activateUser(Long id);
    
    boolean existsByEmail(String email);
    
    // For bulk operations (will be used in Phase 2)
    void bulkCreateUsers(List<User> users);
    
    // For password management
    void forcePasswordChange(Long userId, String newPassword);
}