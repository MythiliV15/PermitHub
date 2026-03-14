package com.permithub.repository;

import com.permithub.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user.id = :userId AND t.isUsed = false AND t.expiryDate > :now")
    Optional<PasswordResetToken> findValidTokenByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.isUsed = true WHERE t.user.id = :userId")
    void invalidateAllUserTokens(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now OR t.isUsed = true")
    void deleteExpiredOrUsedTokens(@Param("now") LocalDateTime now);
}