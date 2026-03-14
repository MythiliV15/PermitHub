package com.permithub.controller.hod;

import com.permithub.dto.hod.BulkUploadResponseDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.security.CustomUserDetails;
import com.permithub.service.hod.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hod/upload")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class BulkUploadController {

    private final BulkUploadService bulkUploadService;

    @PostMapping(value = "/faculty", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BulkUploadResponseDTO>> uploadFaculty(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam("departmentId") Long departmentId) {
        
        BulkUploadResponseDTO response = bulkUploadService.uploadFaculty(
                currentUser.getId(), file, departmentId);
        
        return ResponseEntity.ok(ApiResponse.success("Faculty upload completed", response));
    }

    @PostMapping(value = "/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BulkUploadResponseDTO>> uploadStudents(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("year") Integer year,
            @RequestParam("section") String section) {
        
        BulkUploadResponseDTO response = bulkUploadService.uploadStudents(
                currentUser.getId(), file, departmentId, year, section);
        
        return ResponseEntity.ok(ApiResponse.success("Student upload completed", response));
    }

    @GetMapping("/template/faculty")
    public ResponseEntity<byte[]> downloadFacultyTemplate() {
        byte[] template = bulkUploadService.downloadFacultyTemplate();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faculty_upload_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }

    @GetMapping("/template/student")
    public ResponseEntity<byte[]> downloadStudentTemplate() {
        byte[] template = bulkUploadService.downloadStudentTemplate();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_upload_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUploadHistory(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<Map<String, Object>> history = bulkUploadService.getUploadHistory(
                currentUser.getId(), page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Upload history retrieved successfully", history));
    }

    @GetMapping("/history/{uploadId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadDetails(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long uploadId) {
        
        Map<String, Object> details = bulkUploadService.getUploadDetails(currentUser.getId(), uploadId);
        return ResponseEntity.ok(ApiResponse.success("Upload details retrieved successfully", details));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String uploadType) {
        
        Map<String, Object> validation = bulkUploadService.validateFile(file, uploadType);
        return ResponseEntity.ok(ApiResponse.success("File validation completed", validation));
    }

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> previewUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String uploadType) {
        
        List<Map<String, Object>> preview = bulkUploadService.previewUpload(file, uploadType);
        return ResponseEntity.ok(ApiResponse.success("File preview generated", preview));
    }
}