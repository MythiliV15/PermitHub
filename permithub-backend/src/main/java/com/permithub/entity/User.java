package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 20)
    private String role;
    
    @Column(name = "departmentId")
    private Long departmentId;
    
    @Column(name = "hostelType", length = 10)
    private String hostelType;
    
    @Column(name = "firstLogin", nullable = false)
    @Builder.Default
    private Boolean firstLogin = true;
    
    @Column(name = "isActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
