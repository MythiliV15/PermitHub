package com.permithub.controller.hod;

import com.permithub.dto.hod.BulkUploadResponseDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.service.hod.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/hod/upload")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class BulkUploadController {

    private final BulkUploadService bulkUploadService;

    @PostMapping(value = "/faculty", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BulkUploadResponseDTO>> uploadFaculty(
            @RequestParam("file") MultipartFile file) {
        BulkUploadResponseDTO response = bulkUploadService.uploadFaculty(file);
        return ResponseEntity.ok(ApiResponse.success("Faculty upload completed", response));
    }

    @PostMapping(value = "/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BulkUploadResponseDTO>> uploadStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam("year") Integer year,
            @RequestParam("section") String section) {
        BulkUploadResponseDTO response = bulkUploadService.uploadStudents(file, year, section);
        return ResponseEntity.ok(ApiResponse.success("Student upload completed", response));
    }

    @GetMapping("/template/faculty")
    public ResponseEntity<byte[]> downloadFacultyTemplate() {
        byte[] template = bulkUploadService.downloadFacultyTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faculty_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }

    @GetMapping("/template/student")
    public ResponseEntity<byte[]> downloadStudentTemplate() {
        byte[] template = bulkUploadService.downloadStudentTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }
}