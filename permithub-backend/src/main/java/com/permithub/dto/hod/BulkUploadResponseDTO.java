package com.permithub.dto.hod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResponseDTO {
    
    private String uploadType;
    private String fileName;
    private Integer totalRecords;
    private Integer successfulRecords;
    private Integer failedRecords;
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED
    
    private List<Map<String, Object>> successfulData;
    private List<ErrorRecordDTO> errors;
    
    private String message;
    private Long processingTimeMs;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorRecordDTO {
        private Integer rowNumber;
        private String employeeId;
        private String email;
        private String errorMessage;
        private Map<String, Object> rowData;
    }
}