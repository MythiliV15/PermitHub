package com.permithub.service.hod;

import com.permithub.dto.hod.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FacultyManagementService {
    
    FacultyResponseDTO addFaculty(FacultyRequestDTO request);
    
    BulkUploadResponseDTO bulkUploadFaculty(FacultyBulkUploadDTO bulkUploadDTO);
    
    Page<FacultyResponseDTO> getAllFaculty(String searchTerm, String designation, 
                                           String role, Boolean isActive, Pageable pageable);
    
    FacultyResponseDTO getFacultyById(Long facultyId);
    
    FacultyResponseDTO updateFaculty(Long facultyId, FacultyRequestDTO request);
    
    void deactivateFaculty(Long facultyId);
    
    void activateFaculty(Long facultyId);
    
    FacultyResponseDTO assignRoles(Long facultyId, List<FacultyRoleAssignmentDTO> roles);
    
    Map<String, Object> getFacultyStatistics();
}