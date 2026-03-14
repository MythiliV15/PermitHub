package com.permithub.dto.hod;

import com.permithub.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponseDTO {
    
    private Long requestId;
    private String requestType;
    private String studentName;
    private String registerNumber;
    private String department;
    private Integer year;
    private String section;
    
    private RequestStatus currentStatus;
    private String action;
    private String remarks;
    private String approvedBy;
    private String approverRole;
    private LocalDateTime actionDate;
    
    // Request specific details
    private Object requestDetails; // Will contain leave/OD/outpass specific data
    
    private String message;
    private Boolean success;
}