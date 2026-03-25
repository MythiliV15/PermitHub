package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PasswordResetToken extends BaseEntity {
    
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, length = 255)
    private String token;
    
    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "usedAt")
    private LocalDateTime usedAt;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}