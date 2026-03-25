package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "approval_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApprovalHistory extends BaseEntity {
    
    @Column(name = "requestId", nullable = false)
    private Long requestId; // ID from Leave, OD or Outpass
    
    @Column(name = "requestType", length = 50, nullable = false)
    private String requestType; // LEAVE, OD, OUTPASS
    
    @Column(name = "approverId", nullable = false)
    private Long approverId; // user_id
    
    @Column(name = "approverRole", length = 50, nullable = false)
    private String approverRole; // MENTOR, ADVISOR, HOD, WARDEN
    
    @Column(length = 50, nullable = false)
    private String status; // APPROVED, REJECTED
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
}
