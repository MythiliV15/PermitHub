package com.permithub.controller.hod;

import com.permithub.dto.response.ApiResponse;
import com.permithub.entity.StudentProfile;
import com.permithub.service.hod.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hod/students")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HOD')")
public class StudentManagementController {

    private final StudentManagementService studentManagementService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentProfile>>> getAllStudents(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Boolean isHosteler,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "regNo") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StudentProfile> students;
        if (searchTerm != null || year != null || section != null || isHosteler != null || isActive != null) {
            students = studentManagementService.searchStudents(searchTerm, year, section, isHosteler, isActive, pageable);
        } else {
            students = studentManagementService.getAllStudents(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentProfile>> getStudentById(@PathVariable Long id) {
        StudentProfile student = studentManagementService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student details retrieved", student));
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<StudentProfile>> updateStudentBalance(
            @PathVariable Long id,
            @RequestParam Integer balance) {
        StudentProfile student = studentManagementService.updateStudentBalance(id, balance);
        return ResponseEntity.ok(ApiResponse.success("Student leave balance updated", student));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentStats() {
        Map<String, Object> stats = studentManagementService.getStudentStats();
        return ResponseEntity.ok(ApiResponse.success("Student statistics retrieved", stats));
    }
}
