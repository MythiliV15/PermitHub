package com.permithub.dto.hod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String type; // LEAVE, OD, OUTPASS, FACULTY_ADDED, etc.
    private String description;
    private String studentName;
    private String registerNumber;
    private String status;
    private String timestamp;
    private String actionBy;
}
