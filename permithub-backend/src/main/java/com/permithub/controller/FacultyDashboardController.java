package com.permithub.controller;

import com.permithub.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faculty/dashboard")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyDashboardController {

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<String>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success("Faculty Dashboard Coming Soon!"));
    }
}
