package com.permithub.controller.hod;

import com.permithub.dto.response.ApiResponse;
import com.permithub.entity.FacultyProfile;
import com.permithub.repository.FacultyProfileRepository;
import com.permithub.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hod/profile")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HOD')")
public class HODProfileController {

    private final FacultyProfileRepository facultyProfileRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<FacultyProfile>> getProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        FacultyProfile hod = facultyProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("HOD profile not found"));
        
        return ResponseEntity.ok(ApiResponse.success("HOD profile retrieved successfully", hod));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<FacultyProfile>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody FacultyProfile profileUpdate) {
        
        FacultyProfile existingHOD = facultyProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("HOD profile not found"));
        
        // Update fields from the profile update
        existingHOD.setName(profileUpdate.getName());
        existingHOD.setPhone(profileUpdate.getPhone());
        existingHOD.setDesignation(profileUpdate.getDesignation());
        
        FacultyProfile updated = facultyProfileRepository.save(existingHOD);
        return ResponseEntity.ok(ApiResponse.success("HOD profile updated successfully", updated));
    }
}
