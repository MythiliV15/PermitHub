package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_upload_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BulkUploadHistory extends BaseEntity {
    
    @Column(name = "uploadType", nullable = false, length = 50)
    private String uploadType; // FACULTY, STUDENT
    
    @Column(name = "fileName", nullable = false)
    private String fileName;
    
    @Column(name = "filePath", length = 500)
    private String filePath;
    
    @Column(name = "uploadedBy", nullable = false)
    private Long uploadedBy; // linked to user.id
    
    @Column(name = "departmentId")
    private Long departmentId;
    
    @Column(name = "uploadedDate", nullable = false)
    @Builder.Default
    private LocalDateTime uploadedDate = LocalDateTime.now();
    
    @Column(name = "totalRecords", nullable = false)
    private Integer totalRecords;
    
    @Column(name = "successfulRecords", nullable = false)
    private Integer successfulRecords;
    
    @Column(name = "failedRecords", nullable = false)
    private Integer failedRecords;
    
    @Column(name = "errorLog", columnDefinition = "TEXT")
    private String errorLog; // JSON array of errors
    
    @Column(nullable = false, length = 20)
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED
    
    @Column(name = "processingTimeMs")
    private Long processingTimeMs;
    
    @Column(name = "startTime")
    private LocalDateTime startTime;
    
    @Column(name = "endTime")
    private LocalDateTime endTime;
    
    // Default password info for this upload
    @Column(name = "defaultPasswordUsed")
    private String defaultPasswordUsed; // The default password set for this batch
    
    @Column(name = "passwordResetRequired")
    @Builder.Default
    private Boolean passwordResetRequired = true;
    
    // Additional metadata
    @Column(name = "fileSizeBytes")
    private Long fileSizeBytes;
    
    @Column(name = "originalFilename")
    private String originalFilename;
    
    @Column(name = "contentType")
    private String contentType;
    
    @Column(name = "uploadIp")
    private String uploadIp;
    
    @PrePersist
    protected void onPrePersist() {
        if (uploadedDate == null) {
            uploadedDate = LocalDateTime.now();
        }
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }
}
