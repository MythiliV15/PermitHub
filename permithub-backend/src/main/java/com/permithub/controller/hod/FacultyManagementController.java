package com.permithub.controller.hod;

import com.permithub.dto.hod.*;
import com.permithub.dto.response.ApiResponse;
import com.permithub.service.hod.FacultyManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hod/faculty")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HOD')")
public class FacultyManagementController {

    private final FacultyManagementService facultyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> addFaculty(
            @Valid @RequestBody FacultyRequestDTO request) {
        FacultyResponseDTO response = facultyService.addFaculty(request);
        return ResponseEntity.ok(ApiResponse.success("Faculty added successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FacultyResponseDTO>>> getAllFaculty(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean isActiveFilter = null;
        if (isActive != null && !isActive.isBlank()) {
            isActiveFilter = Boolean.parseBoolean(isActive);
        }
        
        Page<FacultyResponseDTO> faculty = facultyService.getAllFaculty(search, designation, role, isActiveFilter, pageable);
        return ResponseEntity.ok(ApiResponse.success("Faculty retrieved successfully", faculty));
    }

    @GetMapping("/{facultyId}")
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> getFacultyById(@PathVariable Long facultyId) {
        FacultyResponseDTO faculty = facultyService.getFacultyById(facultyId);
        return ResponseEntity.ok(ApiResponse.success("Faculty details retrieved successfully", faculty));
    }

    @PutMapping("/{facultyId}")
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> updateFaculty(
            @PathVariable Long facultyId,
            @Valid @RequestBody FacultyRequestDTO request) {
        FacultyResponseDTO faculty = facultyService.updateFaculty(facultyId, request);
        return ResponseEntity.ok(ApiResponse.success("Faculty updated successfully", faculty));
    }

    @PatchMapping("/{facultyId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateFaculty(@PathVariable Long facultyId) {
        facultyService.deactivateFaculty(facultyId);
        return ResponseEntity.ok(ApiResponse.success("Faculty deactivated successfully"));
    }

    @PatchMapping("/{facultyId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateFaculty(@PathVariable Long facultyId) {
        facultyService.activateFaculty(facultyId);
        return ResponseEntity.ok(ApiResponse.success("Faculty activated successfully"));
    }

    @PostMapping("/{facultyId}/roles")
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> assignRoles(
            @PathVariable Long facultyId,
            @RequestBody List<FacultyRoleAssignmentDTO> roles) {
        FacultyResponseDTO faculty = facultyService.assignRoles(facultyId, roles);
        return ResponseEntity.ok(ApiResponse.success("Roles assigned successfully", faculty));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFacultyStatistics() {
        Map<String, Object> stats = facultyService.getFacultyStatistics();
        return ResponseEntity.ok(ApiResponse.success("Faculty statistics retrieved successfully", stats));
    }

    @GetMapping("/template")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<byte[]> downloadFacultyTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Faculty Template");
        Row header = sheet.createRow(0);
        String[] cols = {"name", "email", "phone", "designation", "employeeId"};
        for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        
        byte[] content = out.toByteArray();
        
        return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faculty_upload_template.xlsx")
          .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
          .header(HttpHeaders.PRAGMA, "no-cache")
          .header(HttpHeaders.EXPIRES, "0")
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .contentLength(content.length)
          .body(content);
    }
}