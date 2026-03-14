package com.permithub.dto.hod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDTO {
    
    @NotNull(message = "Request ID is required")
    private Long requestId;
    
    @NotBlank(message = "Request type is required")
    private String requestType; // LEAVE, OD, OUTPASS
    
    @NotBlank(message = "Action is required")
    private String action; // APPROVE, REJECT
    
    private String remarks;
    
    @Builder.Default
    private Boolean sendNotification = true;
}