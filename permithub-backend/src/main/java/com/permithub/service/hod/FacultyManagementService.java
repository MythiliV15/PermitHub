package com.permithub.service.hod;

import com.permithub.dto.hod.FacultyBulkUploadDTO;
import com.permithub.dto.hod.FacultyRequestDTO;
import com.permithub.dto.hod.FacultyResponseDTO;
import com.permithub.dto.hod.BulkUploadResponseDTO;
import com.permithub.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface FacultyManagementService {
    
    /**
     * Add a single faculty member
     * @param hodId The HOD user ID
     * @param request Faculty details
     * @return Created faculty response
     */
    FacultyResponseDTO addFaculty(Long hodId, FacultyRequestDTO request);
    
    /**
     * Bulk upload faculty via Excel
     * @param hodId The HOD user ID
     * @param bulkUploadDTO Contains file and metadata
     * @return Bulk upload response with success/failure details
     */
    BulkUploadResponseDTO bulkUploadFaculty(Long hodId, FacultyBulkUploadDTO bulkUploadDTO);
    
    /**
     * Download Excel template for faculty upload
     * @return Excel file as byte array
     */
    byte[] downloadFacultyTemplate();
    
    /**
     * Get all faculty in HOD's department with filters
     * @param hodId The HOD user ID
     * @param searchTerm Search by name, email, employee ID
     * @param designation Filter by designation
     * @param role Filter by role
     * @param isActive Filter by active status
     * @param pageable Pagination information
     * @return Page of faculty responses
     */
    Page<FacultyResponseDTO> getAllFaculty(Long hodId, String searchTerm, String designation, 
                                           Role role, Boolean isActive, Pageable pageable);
    
    /**
     * Get faculty by ID
     * @param hodId The HOD user ID
     * @param facultyId Faculty ID to fetch
     * @return Faculty response
     */
    FacultyResponseDTO getFacultyById(Long hodId, Long facultyId);
    
    /**
     * Update faculty details
     * @param hodId The HOD user ID
     * @param facultyId Faculty ID to update
     * @param request Updated faculty details
     * @return Updated faculty response
     */
    FacultyResponseDTO updateFaculty(Long hodId, Long facultyId, FacultyRequestDTO request);
    
    /**
     * Deactivate faculty account
     * @param hodId The HOD user ID
     * @param facultyId Faculty ID to deactivate
     * @param reason Reason for deactivation
     */
    void deactivateFaculty(Long hodId, Long facultyId, String reason);
    
    /**
     * Activate faculty account
     * @param hodId The HOD user ID
     * @param facultyId Faculty ID to activate
     */
    void activateFaculty(Long hodId, Long facultyId);
    
    /**
     * Assign roles to faculty
     * @param hodId The HOD user ID
     * @param facultyId Faculty ID
     * @param roles Set of roles to assign
     * @return Updated faculty response
     */
    FacultyResponseDTO assignRoles(Long hodId, Long facultyId, Set<Role> roles);
    
    /**
     * Get faculty statistics
     * @param hodId The HOD user ID
     * @return Map of statistics
     */
    java.util.Map<String, Object> getFacultyStatistics(Long hodId);
    
    /**
     * Check if employee ID already exists
     * @param employeeId Employee ID to check
     * @return true if exists
     */
    boolean isEmployeeIdExists(String employeeId);
    
    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if exists
     */
    boolean isEmailExists(String email);
}