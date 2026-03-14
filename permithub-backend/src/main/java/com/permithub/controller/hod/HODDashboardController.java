package com.permithub.controller.hod;

import com.permithub.dto.hod.DashboardStatsDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.security.CustomUserDetails;
import com.permithub.service.hod.HODDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hod/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class HODDashboardController {

    private final HODDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        DashboardStatsDTO stats = dashboardService.getDashboardStats(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<Page<Object>>> getRecentActivities(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        Page<Object> activities = dashboardService.getRecentActivities(currentUser.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Recent activities retrieved successfully", activities));
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Object>> getDepartmentOverview(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Object overview = dashboardService.getDepartmentOverview(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Department overview retrieved successfully", overview));
    }

    @GetMapping("/distribution/year")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getYearWiseDistribution(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Long> distribution = dashboardService.getYearWiseStudentDistribution(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Year-wise distribution retrieved successfully", distribution));
    }

    @GetMapping("/distribution/section")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getSectionWiseDistribution(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) Integer year) {
        
        Map<String, Long> distribution = dashboardService.getSectionWiseStudentDistribution(currentUser.getId(), year);
        return ResponseEntity.ok(ApiResponse.success("Section-wise distribution retrieved successfully", distribution));
    }

    @GetMapping("/pending-counts")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPendingCounts(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Long> counts = dashboardService.getPendingApprovalsCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Pending counts retrieved successfully", counts));
    }

    @GetMapping("/faculty-roles")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getFacultyRoleDistribution(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Long> distribution = dashboardService.getFacultyRoleDistribution(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Faculty role distribution retrieved successfully", distribution));
    }
}