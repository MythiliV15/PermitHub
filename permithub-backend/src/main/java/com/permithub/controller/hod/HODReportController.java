package com.permithub.controller.hod;

import com.permithub.dto.response.ApiResponse;
import com.permithub.service.hod.HODReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hod/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class HODReportController {

    private final HODReportService hodReportService;

    @GetMapping("/leaves")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLeaveReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Map<String, Object>> report = hodReportService.getLeaveReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Leave report generated", report));
    }

    @GetMapping("/od")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getODReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            
        List<Map<String, Object>> report = hodReportService.getODReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("OD report generated", report));
    }

    @GetMapping("/outpass")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOutpassReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            
        List<Map<String, Object>> report = hodReportService.getOutpassReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Outpass report generated", report));
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDepartmentPerformance() {
        Map<String, Object> report = hodReportService.getDepartmentPerformanceReport();
        return ResponseEntity.ok(ApiResponse.success("Performance report generated", report));
    }
}
