package com.permithub.controller.hod;

import com.permithub.dto.hod.SemesterDTO;
import com.permithub.dto.hod.SemesterPromotionDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.service.hod.SemesterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hod/semester")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HOD')")
public class SemesterManagementController {

    private final SemesterService semesterService;

    @PostMapping
    public ResponseEntity<ApiResponse<SemesterDTO>> createSemester(@Valid @RequestBody SemesterDTO semesterDTO) {
        SemesterDTO created = semesterService.createSemester(semesterDTO);
        return ResponseEntity.ok(ApiResponse.success("Semester created successfully", created));
    }

    @PutMapping("/{semesterId}")
    public ResponseEntity<ApiResponse<SemesterDTO>> updateSemester(
            @PathVariable Long semesterId, @Valid @RequestBody SemesterDTO semesterDTO) {
        SemesterDTO updated = semesterService.updateSemester(semesterId, semesterDTO);
        return ResponseEntity.ok(ApiResponse.success("Semester updated successfully", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SemesterDTO>>> getAllSemesters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "year") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SemesterDTO> semesters = semesterService.getAllSemesters(pageable);
        return ResponseEntity.ok(ApiResponse.success("Semesters retrieved successfully", semesters));
    }

    @GetMapping("/{semesterId}")
    public ResponseEntity<ApiResponse<SemesterDTO>> getSemesterById(@PathVariable Long semesterId) {
        SemesterDTO semester = semesterService.getSemesterById(semesterId);
        return ResponseEntity.ok(ApiResponse.success("Semester details retrieved successfully", semester));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<SemesterDTO>> getActiveSemester() {
        SemesterDTO active = semesterService.getActiveSemester();
        return ResponseEntity.ok(ApiResponse.success("Active semester retrieved successfully", active));
    }

    @PostMapping("/{semesterId}/activate")
    public ResponseEntity<ApiResponse<SemesterDTO>> activateSemester(@PathVariable Long semesterId) {
        SemesterDTO activated = semesterService.activateSemester(semesterId);
        return ResponseEntity.ok(ApiResponse.success("Semester activated successfully", activated));
    }

    @PostMapping("/{semesterId}/deactivate")
    public ResponseEntity<ApiResponse<SemesterDTO>> deactivateSemester(@PathVariable Long semesterId) {
        SemesterDTO deactivated = semesterService.deactivateSemester(semesterId);
        return ResponseEntity.ok(ApiResponse.success("Semester deactivated successfully", deactivated));
    }

    @PostMapping("/promote")
    public ResponseEntity<ApiResponse<Map<String, Object>>> promoteStudents(@Valid @RequestBody SemesterPromotionDTO promotionDTO) {
        Map<String, Object> result = semesterService.promoteStudents(promotionDTO);
        return ResponseEntity.ok(ApiResponse.success("Student promotion completed", result));
    }

    @PostMapping("/reset-leave-balance")
    public ResponseEntity<ApiResponse<Integer>> resetLeaveBalance(@RequestParam(defaultValue = "20") Integer newBalance) {
        int resetCount = semesterService.resetLeaveBalance(newBalance);
        return ResponseEntity.ok(ApiResponse.success("Leave balance reset successfully", resetCount));
    }

    @GetMapping("/promotion-eligibility")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPromotionEligibility(
            @RequestParam Integer year, @RequestParam(required = false) String section) {
        List<Map<String, Object>> eligibility = semesterService.getPromotionEligibility(year, section);
        return ResponseEntity.ok(ApiResponse.success("Promotion eligibility retrieved successfully", eligibility));
    }

    @PutMapping("/{semesterId}/leave-limit")
    public ResponseEntity<ApiResponse<SemesterDTO>> setDefaultLeaveLimit(
            @PathVariable Long semesterId, @RequestParam Integer leaveLimit) {
        SemesterDTO updated = semesterService.setDefaultLeaveLimit(semesterId, leaveLimit);
        return ResponseEntity.ok(ApiResponse.success("Default leave limit updated successfully", updated));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSemesterStatistics() {
        Map<String, Object> stats = semesterService.getSemesterStatistics();
        return ResponseEntity.ok(ApiResponse.success("Semester statistics retrieved successfully", stats));
    }
}