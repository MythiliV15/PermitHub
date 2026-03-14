package com.permithub.service.hod;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.permithub.dto.hod.SemesterDTO;
import com.permithub.dto.hod.SemesterPromotionDTO;

public interface SemesterService {
    
    /**
     * Create a new semester
     * @param hodId The HOD user ID
     * @param semesterDTO Semester details
     * @return Created semester
     */
    SemesterDTO createSemester(Long hodId, SemesterDTO semesterDTO);
    
    /**
     * Update semester details
     * @param hodId The HOD user ID
     * @param semesterId Semester ID to update
     * @param semesterDTO Updated details
     * @return Updated semester
     */
    SemesterDTO updateSemester(Long hodId, Long semesterId, SemesterDTO semesterDTO);
    
    /**
     * Get all semesters for HOD's department
     * @param hodId The HOD user ID
     * @param pageable Pagination information
     * @return Page of semesters
     */
    Page<SemesterDTO> getAllSemesters(Long hodId, Pageable pageable);
    
    /**
     * Get semester by ID
     * @param hodId The HOD user ID
     * @param semesterId Semester ID
     * @return Semester details
     */
    SemesterDTO getSemesterById(Long hodId, Long semesterId);
    
    /**
     * Get active semester for department
     * @param hodId The HOD user ID
     * @return Active semester
     */
    SemesterDTO getActiveSemester(Long hodId);
    
    /**
     * Activate a semester
     * @param hodId The HOD user ID
     * @param semesterId Semester ID to activate
     * @return Activated semester
     */
    SemesterDTO activateSemester(Long hodId, Long semesterId);
    
    /**
     * Deactivate current active semester
     * @param hodId The HOD user ID
     * @param semesterId Semester ID to deactivate
     * @return Deactivated semester
     */
    SemesterDTO deactivateSemester(Long hodId, Long semesterId);
    
    /**
     * Promote students to next semester
     * @param hodId The HOD user ID
     * @param promotionDTO Promotion details
     * @return Promotion results with statistics
     */
    Map<String, Object> promoteStudents(Long hodId, SemesterPromotionDTO promotionDTO);
    
    /**
     * Reset leave balance for all students in department
     * @param hodId The HOD user ID
     * @param newBalance New leave balance (default 20)
     * @return Number of students updated
     */
    int resetLeaveBalance(Long hodId, Integer newBalance);
    
    /**
     * Get promotion eligibility list
     * @param hodId The HOD user ID
     * @param year Current year
     * @param section Section (optional)
     * @return List of students with eligibility status
     */
    List<Map<String, Object>> getPromotionEligibility(Long hodId, Integer year, String section);
    
    /**
     * Set default leave limit for semester
     * @param hodId The HOD user ID
     * @param semesterId Semester ID
     * @param leaveLimit New leave limit
     * @return Updated semester
     */
    SemesterDTO setDefaultLeaveLimit(Long hodId, Long semesterId, Integer leaveLimit);
    
    /**
     * Get semester statistics
     * @param hodId The HOD user ID
     * @return Semester statistics
     */
    Map<String, Object> getSemesterStatistics(Long hodId);
}