package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "google_sheets_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoogleSheetsConfig extends BaseEntity {
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(name = "configName", length = 100, nullable = false)
    private String configName; // BULK_UPLOAD, ATTENDANCE_SYNC
    
    @Column(name = "spreadsheetId", length = 255, nullable = false)
    private String spreadsheetId;
    
    @Column(name = "sheetName", length = 100)
    private String sheetName;
    
    @Column(name = "rangeAddress", length = 50)
    private String rangeAddress;
    
    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "apiKey", length = 255)
    private String apiKey;
    
    @Column(name = "lastSyncedAt")
    private LocalDateTime lastSyncedAt;
}
