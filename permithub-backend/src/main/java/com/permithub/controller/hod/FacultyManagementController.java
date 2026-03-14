package com.permithub.controller.hod;

import com.permithub.dto.hod.FacultyRequestDTO;
import com.permithub.dto.hod.FacultyResponseDTO;
import com.permithub.dto.hod.BulkUploadResponseDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.entity.Role;
import com.permithub.security.CustomUserDetails;
import com.permithub.service.hod.FacultyManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/hod/faculty")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class FacultyManagementController {

    private final FacultyManagementService facultyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> addFaculty(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody FacultyRequestDTO request) {
        
        FacultyResponseDTO response = facultyService.addFaculty(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Faculty added successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FacultyResponseDTO>>> getAllFaculty(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FacultyResponseDTO> faculty = facultyService.getAllFaculty(
                currentUser.getId(), search, designation, role, isActive, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Faculty retrieved successfully", faculty));
    }

    @GetMapping("/{facultyId}")
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> getFacultyById(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long facultyId) {
        
        FacultyResponseDTO faculty = facultyService.getFacultyById(currentUser.getId(), facultyId);
        return ResponseEntity.ok(ApiResponse.success("Faculty details retrieved successfully", faculty));
    }

    @PutMapping("/{facultyId}")
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> updateFaculty(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long facultyId,
            @Valid @RequestBody FacultyRequestDTO request) {
        
        FacultyResponseDTO faculty = facultyService.updateFaculty(currentUser.getId(), facultyId, request);
        return ResponseEntity.ok(ApiResponse.success("Faculty updated successfully", faculty));
    }

    @PatchMapping("/{facultyId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateFaculty(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long facultyId,
            @RequestParam(required = false) String reason) {
        
        facultyService.deactivateFaculty(currentUser.getId(), facultyId, reason);
        return ResponseEntity.ok(ApiResponse.success("Faculty deactivated successfully"));
    }

    @PatchMapping("/{facultyId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateFaculty(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long facultyId) {
        
        facultyService.activateFaculty(currentUser.getId(), facultyId);
        return ResponseEntity.ok(ApiResponse.success("Faculty activated successfully"));
    }

    @PostMapping("/{facultyId}/roles")
    public ResponseEntity<ApiResponse<FacultyResponseDTO>> assignRoles(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long facultyId,
            @RequestBody Set<Role> roles) {
        
        FacultyResponseDTO faculty = facultyService.assignRoles(currentUser.getId(), facultyId, roles);
        return ResponseEntity.ok(ApiResponse.success("Roles assigned successfully", faculty));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFacultyStatistics(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> stats = facultyService.getFacultyStatistics(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Faculty statistics retrieved successfully", stats));
    }

    @GetMapping("/check/employee/{employeeId}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmployeeId(
            @PathVariable String employeeId) {
        
        boolean exists = facultyService.isEmployeeIdExists(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Employee ID check completed", exists));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @PathVariable String email) {
        
        boolean exists = facultyService.isEmailExists(email);
        return ResponseEntity.ok(ApiResponse.success("Email check completed", exists));
    }
}