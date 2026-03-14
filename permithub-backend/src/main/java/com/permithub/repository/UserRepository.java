package com.permithub.repository;

import com.permithub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveByEmail(@Param("email") String email);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt, u.lastLoginIp = :lastLoginIp WHERE u.email = :email")
    void updateLastLogin(@Param("email") String email, 
                        @Param("lastLoginAt") LocalDateTime lastLoginAt, 
                        @Param("lastLoginIp") String lastLoginIp);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    void updatePassword(@Param("email") String email, @Param("password") String password);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isFirstLogin = false WHERE u.email = :email")
    void disableFirstLogin(@Param("email") String email);
}
