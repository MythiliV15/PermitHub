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
public class DashboardStatsDTO {
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    
    // Counts
    private Long totalStudents;
    private Long totalFaculty;
    private Long totalMentors;
    private Long totalClassAdvisors;
    private Long totalEventCoordinators;
    
    // Pending approvals
    private Long pendingLeaveApprovals;
    private Long pendingODApprovals;
    private Long pendingOutpassApprovals;
    private Long totalPendingApprovals;
    
    // Student distribution
    private Map<String, Long> yearWiseStudentDistribution;
    private Map<String, Long> sectionWiseStudentDistribution;
    private Long hostelersCount;
    private Long dayScholarsCount;
    
    // Recent activities
    private List<RecentActivityDTO> recentActivities;
    
    // Charts data
    private Map<String, Long> leaveRequestsByMonth;
    private Map<String, Long> odRequestsByType;
    private Map<String, Long> approvalsByStatus;
}
