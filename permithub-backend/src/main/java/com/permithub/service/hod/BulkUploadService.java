package com.permithub.service.hod;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.permithub.dto.hod.BulkUploadResponseDTO;

public interface BulkUploadService {
    
    /**
     * Bulk upload faculty via Excel
     * @param hodId The HOD user ID
     * @param file Excel file containing faculty data
     * @param departmentId Department ID
     * @return Bulk upload response with success/failure details
     */
    BulkUploadResponseDTO uploadFaculty(Long hodId, MultipartFile file, Long departmentId);
    
    /**
     * Bulk upload students via Excel (for Class Advisor)
     * @param hodId The HOD user ID (or Class Advisor ID)
     * @param file Excel file containing student data
     * @param departmentId Department ID
     * @param year Student year
     * @param section Student section
     * @return Bulk upload response with success/failure details
     */
    BulkUploadResponseDTO uploadStudents(Long hodId, MultipartFile file, Long departmentId, Integer year, String section);
    
    /**
     * Download faculty upload template
     * @return Excel template as byte array
     */
    byte[] downloadFacultyTemplate();
    
    /**
     * Download student upload template
     * @return Excel template as byte array
     */
    byte[] downloadStudentTemplate();
    
    /**
     * Get bulk upload history
     * @param hodId The HOD user ID
     * @param page Page number
     * @param size Page size
     * @return List of upload history records
     */
    List<Map<String, Object>> getUploadHistory(Long hodId, int page, int size);
    
    /**
     * Get upload details by ID
     * @param hodId The HOD user ID
     * @param uploadId Upload history ID
     * @return Upload details with error log
     */
    Map<String, Object> getUploadDetails(Long hodId, Long uploadId);
    
    /**
     * Validate Excel file before upload
     * @param file Excel file to validate
     * @param uploadType Type of upload (FACULTY/STUDENT)
     * @return Validation results with errors if any
     */
    Map<String, Object> validateFile(MultipartFile file, String uploadType);
    
    /**
     * Get sample data for preview
     * @param file Excel file
     * @param uploadType Type of upload
     * @return First few rows for preview
     */
    List<Map<String, Object>> previewUpload(MultipartFile file, String uploadType);
}