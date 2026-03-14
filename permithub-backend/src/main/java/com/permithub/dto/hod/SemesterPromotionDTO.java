package com.permithub.dto.hod;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterPromotionDTO {
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    @NotNull(message = "From year is required")
    private Integer fromYear;
    
    private String fromSection; // Optional - if null, promote all sections
    
    @NotNull(message = "To year is required")
    private Integer toYear;
    
    private String toSection;
    
    @NotNull(message = "New semester ID is required")
    private Long newSemesterId;
    
    @Builder.Default
    private Integer newLeaveBalance = 20;
    
    @Builder.Default
    private Boolean promoteOnlyPassedStudents = true;
    
    private List<Long> excludeStudentIds; // Students to exclude from promotion
    
    private String remarks;
}