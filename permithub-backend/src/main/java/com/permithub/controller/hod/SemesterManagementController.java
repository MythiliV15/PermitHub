package com.permithub.controller.hod;

import com.permithub.dto.hod.SemesterDTO;
import com.permithub.dto.hod.SemesterPromotionDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.security.CustomUserDetails;
import com.permithub.service.hod.SemesterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hod/semester")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class SemesterManagementController {

    private final SemesterService semesterService;

    @PostMapping
    public ResponseEntity<ApiResponse<SemesterDTO>> createSemester(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SemesterDTO semesterDTO) {
        
        SemesterDTO created = semesterService.createSemester(currentUser.getId(), semesterDTO);
        return ResponseEntity.ok(ApiResponse.success("Semester created successfully", created));
    }

    @PutMapping("/{semesterId}")
    public ResponseEntity<ApiResponse<SemesterDTO>> updateSemester(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long semesterId,
            @Valid @RequestBody SemesterDTO semesterDTO) {
        
        SemesterDTO updated = semesterService.updateSemester(currentUser.getId(), semesterId, semesterDTO);
        return ResponseEntity.ok(ApiResponse.success("Semester updated successfully", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SemesterDTO>>> getAllSemesters(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "year") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SemesterDTO> semesters = semesterService.getAllSemesters(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Semesters retrieved successfully", semesters));
    }

    @GetMapping("/{semesterId}")
    public ResponseEntity<ApiResponse<SemesterDTO>> getSemesterById(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long semesterId) {
        
        SemesterDTO semester = semesterService.getSemesterById(currentUser.getId(), semesterId);
        return ResponseEntity.ok(ApiResponse.success("Semester details retrieved successfully", semester));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<SemesterDTO>> getActiveSemester(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        SemesterDTO active = semesterService.getActiveSemester(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Active semester retrieved successfully", active));
    }

    @PostMapping("/{semesterId}/activate")
    public ResponseEntity<ApiResponse<SemesterDTO>> activateSemester(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long semesterId) {
        
        SemesterDTO activated = semesterService.activateSemester(currentUser.getId(), semesterId);
        return ResponseEntity.ok(ApiResponse.success("Semester activated successfully", activated));
    }

    @PostMapping("/{semesterId}/deactivate")
    public ResponseEntity<ApiResponse<SemesterDTO>> deactivateSemester(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long semesterId) {
        
        SemesterDTO deactivated = semesterService.deactivateSemester(currentUser.getId(), semesterId);
        return ResponseEntity.ok(ApiResponse.success("Semester deactivated successfully", deactivated));
    }

    @PostMapping("/promote")
    public ResponseEntity<ApiResponse<Map<String, Object>>> promoteStudents(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SemesterPromotionDTO promotionDTO) {
        
        Map<String, Object> result = semesterService.promoteStudents(currentUser.getId(), promotionDTO);
        return ResponseEntity.ok(ApiResponse.success("Student promotion completed", result));
    }

    @PostMapping("/reset-leave-balance")
    public ResponseEntity<ApiResponse<Integer>> resetLeaveBalance(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "20") Integer newBalance) {
        
        int resetCount = semesterService.resetLeaveBalance(currentUser.getId(), newBalance);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Leave balance reset for %d students", resetCount), resetCount));
    }

    @GetMapping("/promotion-eligibility")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPromotionEligibility(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam Integer year,
            @RequestParam(required = false) String section) {
        
        List<Map<String, Object>> eligibility = semesterService.getPromotionEligibility(
                currentUser.getId(), year, section);
        
        return ResponseEntity.ok(ApiResponse.success("Promotion eligibility retrieved successfully", eligibility));
    }

    @PutMapping("/{semesterId}/leave-limit")
    public ResponseEntity<ApiResponse<SemesterDTO>> setDefaultLeaveLimit(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long semesterId,
            @RequestParam Integer leaveLimit) {
        
        SemesterDTO updated = semesterService.setDefaultLeaveLimit(currentUser.getId(), semesterId, leaveLimit);
        return ResponseEntity.ok(ApiResponse.success("Default leave limit updated successfully", updated));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSemesterStatistics(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> stats = semesterService.getSemesterStatistics(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Semester statistics retrieved successfully", stats));
    }
}