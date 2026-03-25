package com.permithub.service.hod;

import com.permithub.dto.hod.*;
import com.permithub.entity.FacultyProfile;
import com.permithub.entity.FacultyRole;
import com.permithub.entity.User;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.FacultyProfileRepository;
import com.permithub.repository.FacultyRoleRepository;
import com.permithub.repository.UserRepository;
import com.permithub.security.SecurityUtils;
import com.permithub.service.EmailService;
import com.permithub.util.ExcelHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacultyManagementServiceImpl implements FacultyManagementService {

    private final UserRepository userRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final FacultyRoleRepository facultyRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelHelper excelHelper;
    private final EmailService emailService;

    private static final String DEFAULT_PASSWORD = "Welcome@123";

    @Override
    public FacultyResponseDTO addFaculty(FacultyRequestDTO request) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            // Fallback: lookup department from HOD's own profile if context is missing
            deptId = facultyProfileRepository.findByUserId(SecurityUtils.getCurrentUserId())
                    .map(FacultyProfile::getDepartmentId)
                    .orElseThrow(() -> new BadRequestException("Could not identify your department. Please re-login."));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        if (facultyProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID already exists: " + request.getEmployeeId());
        }

        // 1. Create User
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role("FACULTY")
                .departmentId(deptId)
                .firstLogin(true)
                .isActive(true)
                .build();
        user = userRepository.save(user);

        // 2. Create FacultyProfile
        FacultyProfile profile = FacultyProfile.builder()
                .userId(user.getId())
                .name(request.getName())
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .employeeId(request.getEmployeeId())
                .departmentId(deptId)
                .build();
        facultyProfileRepository.save(profile);

        // 3. Send Email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), profile.getName(), DEFAULT_PASSWORD);
        } catch (Exception e) {
            log.error("Email delivery failed for {}: {}", user.getEmail(), e.getMessage());
        }

        return mapToResponse(user, profile);
    }

    @Override
    public BulkUploadResponseDTO bulkUploadFaculty(FacultyBulkUploadDTO bulkUploadDTO) {
        List<FacultyRequestDTO> requests = excelHelper.parseFacultyExcel(bulkUploadDTO.getFile());
        int success = 0;
        for (FacultyRequestDTO req : requests) {
            try {
                addFaculty(req);
                success++;
            } catch (Exception e) {
                log.error("Bulk upload row error: {}", e.getMessage());
            }
        }
        return BulkUploadResponseDTO.builder()
                .status(success == requests.size() ? "SUCCESS" : "PARTIAL")
                .totalRecords(requests.size())
                .successfulRecords(success)
                .build();
    }

    @Override
    public Page<FacultyResponseDTO> getAllFaculty(String searchTerm, String designation, 
                                                   String role, Boolean isActive, Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            deptId = facultyProfileRepository.findByUserId(SecurityUtils.getCurrentUserId())
                    .map(FacultyProfile::getDepartmentId)
                    .orElse(null);
        }
        
        if (deptId == null) {
            log.warn("GET ALL FACULTY: Department ID is null in context and fallback failed.");
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        List<FacultyProfile> profiles = facultyProfileRepository.findByDepartmentId(deptId);
        
        List<FacultyResponseDTO> filtered = profiles.stream()
                .map(p -> {
                    User u = userRepository.findById(p.getUserId()).orElse(null);
                    return mapToResponse(u, p);
                })
                .filter(Objects::nonNull)
                .filter(res -> isActive == null || res.getIsActive().equals(isActive))
                .filter(res -> designation == null || designation.isBlank() ||
                        (res.getDesignation() != null && res.getDesignation().equalsIgnoreCase(designation)))
                .filter(res -> role == null || role.isBlank() ||
                        (res.getRoles() != null && res.getRoles().contains(role)))
                .filter(res -> searchTerm == null || searchTerm.isBlank() ||
                        (res.getName() != null && res.getName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (res.getEmail() != null && res.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (res.getEmployeeId() != null && res.getEmployeeId().toLowerCase().contains(searchTerm.toLowerCase())))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public FacultyResponseDTO getFacultyById(Long facultyId) {
        FacultyProfile profile = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        User user = userRepository.findById(profile.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User associated with profile not found"));
        return mapToResponse(user, profile);
    }

    @Override
    public FacultyResponseDTO updateFaculty(Long facultyId, FacultyRequestDTO request) {
        FacultyProfile profile = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        
        profile.setName(request.getName());
        profile.setPhone(request.getPhone());
        profile.setDesignation(request.getDesignation());
        profile.setEmployeeId(request.getEmployeeId());
        facultyProfileRepository.save(profile);

        User user = userRepository.findById(profile.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User associated with faculty not found"));
        return mapToResponse(user, profile);
    }

    @Override
    public void deactivateFaculty(Long facultyId) {
        FacultyProfile profile = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        User user = userRepository.findById(profile.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User associated with faculty not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void activateFaculty(Long facultyId) {
        FacultyProfile profile = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        User user = userRepository.findById(profile.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User associated with faculty not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public FacultyResponseDTO assignRoles(Long facultyId, List<FacultyRoleAssignmentDTO> roles) {
        FacultyProfile profile = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        
        facultyRoleRepository.deactivateAllForFaculty(profile.getId());
        
        for (FacultyRoleAssignmentDTO r : roles) {
            FacultyRole fr = facultyRoleRepository.findByFacultyIdAndRoleName(profile.getId(), r.getRoleName())
                    .orElse(FacultyRole.builder()
                            .facultyId(profile.getId())
                            .roleName(r.getRoleName())
                            .build());
            fr.setIsActive(true);
            fr.setConfig(r.getConfig());
            facultyRoleRepository.save(fr);
        }

        User user = userRepository.findById(profile.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User associated with faculty not found"));
        return mapToResponse(user, profile);
    }

    @Override
    public Map<String, Object> getFacultyStatistics() {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFaculty", facultyProfileRepository.findByDepartmentId(deptId).size());
        return stats;
    }

    private FacultyResponseDTO mapToResponse(User user, FacultyProfile profile) {
        if (profile == null) return null;
        
        List<FacultyRole> roles = facultyRoleRepository.findByFacultyIdAndIsActiveTrue(profile.getId());
        Set<String> roleNames = roles.stream()
                .filter(r -> r.getRoleName() != null)
                .map(FacultyRole::getRoleName)
                .collect(Collectors.toSet());
        
        List<FacultyRoleAssignmentDTO> assignments = roles.stream()
                .map(r -> FacultyRoleAssignmentDTO.builder()
                        .roleName(r.getRoleName())
                        .config(r.getConfig())
                        .build())
                .collect(Collectors.toList());

        return FacultyResponseDTO.builder()
                .id(profile.getId())
                .userId(user != null ? user.getId() : null)
                .employeeId(profile.getEmployeeId())
                .name(profile.getName())
                .fullName(profile.getName())
                .email(user != null ? user.getEmail() : null)
                .phone(profile.getPhone())
                .designation(profile.getDesignation())
                .profilePicPath(profile.getProfilePicPath())
                .profilePicture(profile.getProfilePicPath())
                .roles(roleNames)
                .roleAssignments(assignments)
                .isActive(user != null ? Boolean.TRUE.equals(user.getIsActive()) : false)
                .isFirstLogin(user != null ? Boolean.TRUE.equals(user.getFirstLogin()) : false)
                .createdAt(profile.getCreatedAt())
                .build();
    }
}