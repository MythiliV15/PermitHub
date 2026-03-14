package com.permithub.service.hod;

import com.permithub.dto.hod.DashboardStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HODDashboardService {
    
    /**
     * Get dashboard statistics for HOD
     * @param hodId The HOD user ID
     * @return DashboardStatsDTO with all statistics
     */
    DashboardStatsDTO getDashboardStats(Long hodId);
    
    /**
     * Get recent activities for HOD dashboard
     * @param hodId The HOD user ID
     * @param pageable Pagination information
     * @return Page of recent activities
     */
    Page<Object> getRecentActivities(Long hodId, Pageable pageable);
    
    /**
     * Get department overview
     * @param hodId The HOD user ID
     * @return Department overview data
     */
    Object getDepartmentOverview(Long hodId);
    
    /**
     * Get year-wise student distribution
     * @param hodId The HOD user ID
     * @return Map of year to student count
     */
    java.util.Map<String, Long> getYearWiseStudentDistribution(Long hodId);
    
    /**
     * Get section-wise student distribution for a specific year
     * @param hodId The HOD user ID
     * @param year The academic year
     * @return Map of section to student count
     */
    java.util.Map<String, Long> getSectionWiseStudentDistribution(Long hodId, Integer year);
    
    /**
     * Get pending approvals count by type
     * @param hodId The HOD user ID
     * @return Map of request type to pending count
     */
    java.util.Map<String, Long> getPendingApprovalsCount(Long hodId);
    
    /**
     * Get faculty distribution by role
     * @param hodId The HOD user ID
     * @return Map of role to faculty count
     */
    java.util.Map<String, Long> getFacultyRoleDistribution(Long hodId);
}