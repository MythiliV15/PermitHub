package com.permithub.controller.hod;

import com.permithub.dto.response.ApiResponse;
import com.permithub.entity.HOD;
import com.permithub.repository.HODRepository;
import com.permithub.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hod/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOD')")
public class HODProfileController {

    private final HODRepository hodRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<HOD>> getProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        HOD hod = hodRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("HOD profile not found"));
        
        return ResponseEntity.ok(ApiResponse.success("HOD profile retrieved successfully", hod));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<HOD>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody HOD profileUpdate) {
        
        HOD existingHOD = hodRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("HOD profile not found"));
        
        // Update fields
        existingHOD.setFullName(profileUpdate.getFullName());
        existingHOD.setPhoneNumber(profileUpdate.getPhoneNumber());
        existingHOD.setOfficeLocation(profileUpdate.getOfficeLocation());
        existingHOD.setDesignation(profileUpdate.getDesignation());
        existingHOD.setQualification(profileUpdate.getQualification());
        existingHOD.setSpecialization(profileUpdate.getSpecialization());
        existingHOD.setCabinNumber(profileUpdate.getCabinNumber());
        existingHOD.setOfficePhone(profileUpdate.getOfficePhone());
        
        HOD updated = hodRepository.save(existingHOD);
        return ResponseEntity.ok(ApiResponse.success("HOD profile updated successfully", updated));
    }
}
