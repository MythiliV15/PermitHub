package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_upload_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUploadHistory extends BaseEntity {
    
    @Column(name = "upload_type", nullable = false, length = 50)
    private String uploadType; // FACULTY, STUDENT
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Column(name = "uploaded_date", nullable = false)
    private LocalDateTime uploadedDate;
    
    @Column(name = "total_records", nullable = false)
    private Integer totalRecords;
    
    @Column(name = "successful_records", nullable = false)
    private Integer successfulRecords;
    
    @Column(name = "failed_records", nullable = false)
    private Integer failedRecords;
    
    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog; // JSON array of errors
    
    @Column(nullable = false, length = 20)
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    // Default password info for this upload
    @Column(name = "default_password_used")
    private String defaultPasswordUsed; // The default password set for this batch
    
    @Column(name = "password_reset_required")
    @Builder.Default
    private Boolean passwordResetRequired = true;
    
    // Additional metadata
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "original_filename")
    private String originalFilename;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "upload_ip")
    private String uploadIp;
    
    // Helper methods
    public double getSuccessRate() {
        if (totalRecords == 0) return 0;
        return (successfulRecords * 100.0) / totalRecords;
    }
    
    public boolean isCompleteSuccess() {
        return totalRecords.equals(successfulRecords) && failedRecords == 0;
    }
    
    public boolean isPartialSuccess() {
        return successfulRecords > 0 && failedRecords > 0;
    }
    
    public boolean isFailed() {
        return successfulRecords == 0 && failedRecords > 0;
    }
    
    public void addError(String error) {
        // This would be implemented with JSON processing
    }
    
    @PrePersist
    protected void onCreate() {
        if (uploadedDate == null) {
            uploadedDate = LocalDateTime.now();
        }
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (endTime == null && status != null && (status.equals("SUCCESS") || status.equals("FAILED") || status.equals("PARTIAL_SUCCESS"))) {
            endTime = LocalDateTime.now();
            if (startTime != null) {
                processingTimeMs = java.time.Duration.between(startTime, endTime).toMillis();
            }
        }
    }
}
