package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "faculty_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacultyRole extends BaseEntity {
    
    @Column(name = "facultyId", nullable = false)
    private Long facultyId;
    
    @Column(name = "roleName", nullable = false, length = 30)
    private String roleName;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", columnDefinition = "LONGTEXT")
    private Map<String, Object> config;
    
    @Column(name = "isActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "assignedAt", nullable = false)
    @Builder.Default
    private LocalDateTime assignedAt = LocalDateTime.now();
}
