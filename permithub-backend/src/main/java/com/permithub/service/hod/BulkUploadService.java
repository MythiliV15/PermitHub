package com.permithub.service.hod;

import com.permithub.dto.hod.BulkUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BulkUploadService {
    BulkUploadResponseDTO uploadFaculty(MultipartFile file);
    BulkUploadResponseDTO uploadStudents(MultipartFile file, Integer year, String section);
    byte[] downloadFacultyTemplate();
    byte[] downloadStudentTemplate();
    List<Map<String, Object>> getUploadHistory(int page, int size);
    Map<String, Object> getUploadDetails(Long uploadId);
    Map<String, Object> validateFile(MultipartFile file, String uploadType);
    List<Map<String, Object>> previewUpload(MultipartFile file, String uploadType);
}