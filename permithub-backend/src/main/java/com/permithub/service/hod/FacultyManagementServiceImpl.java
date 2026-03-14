package com.permithub.service.hod;
import com.permithub.service.EmailService;

import com.permithub.dto.hod.*;
import com.permithub.entity.*;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import com.permithub.util.ExcelHelper;
import com.permithub.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacultyManagementServiceImpl implements FacultyManagementService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelHelper excelHelper;
    private final PasswordGenerator passwordGenerator;
    private final EmailService emailService;

    private static final String DEFAULT_PASSWORD = "Welcome@123";
    private static final int DEFAULT_MAX_MENTEES = 20;

    @Override
    public FacultyResponseDTO addFaculty(Long hodId, FacultyRequestDTO request) {
        log.info("Adding new faculty by HOD ID: {}", hodId);
        
        // Validate HOD
        Faculty hod = getHodFaculty(hodId);
        
        // Validate department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        // Check if department belongs to HOD
        if (!department.getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("You can only add faculty to your own department");
        }
        
        // Check for existing employee ID
        if (facultyRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID already exists: " + request.getEmployeeId());
        }
        
        // Check for existing email (checking via userRepository which covers all users including students/faculty)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        
        // Create faculty directly (using SuperBuilder which handles User fields)
        Faculty faculty = Faculty.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .isFirstLogin(true)
                .emailVerified(false)
                .isActive(true)
                .roles(request.getRoles() != null ? request.getRoles() : new HashSet<>(Collections.singletonList(Role.FACULTY)))
                .employeeId(request.getEmployeeId())
                .department(department)
                .designation(request.getDesignation())
                .qualification(request.getQualification())
                .experienceYears(request.getExperienceYears())
                .joiningDate(request.getJoiningDate())
                .isMentor(request.getRoles() != null && request.getRoles().contains(Role.FACULTY_MENTOR))
                .isClassAdvisor(request.getRoles() != null && request.getRoles().contains(Role.FACULTY_CLASS_ADVISOR))
                .isEventCoordinator(request.getRoles() != null && request.getRoles().contains(Role.FACULTY_EVENT_COORDINATOR))
                .maxMentees(request.getMaxMentees() != null ? request.getMaxMentees() : DEFAULT_MAX_MENTEES)
                .currentMentees(0)
                .specialization(request.getSpecialization())
                .cabinNumber(request.getCabinNumber())
                .officePhone(request.getOfficePhone())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .bloodGroup(request.getBloodGroup())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();
        
        faculty = facultyRepository.save(faculty);
        
        // Update department faculty count
        department.setTotalFaculty(department.getTotalFaculty() + 1);
        departmentRepository.save(department);
        
        // Send welcome email
        try {
            emailService.sendWelcomeEmail(request.getEmail(), request.getFullName(), DEFAULT_PASSWORD);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", request.getEmail(), e);
        }
        
        log.info("Faculty added successfully with ID: {}", faculty.getId());
        
        return mapToResponseDTO(faculty);
    }

    @Override
    public BulkUploadResponseDTO bulkUploadFaculty(Long hodId, FacultyBulkUploadDTO bulkUploadDTO) {
        log.info("Processing bulk faculty upload by HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Department department = hod.getDepartment();
        
        BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                .uploadType("FACULTY")
                .fileName(bulkUploadDTO.getFile().getOriginalFilename())
                .status("PROCESSING")
                .build();
        
        List<BulkUploadResponseDTO.ErrorRecordDTO> errors = new ArrayList<>();
        List<FacultyResponseDTO> successful = new ArrayList<>();
        
        try {
            // Parse Excel file
            List<FacultyRequestDTO> facultyList = excelHelper.parseFacultyExcel(
                    bulkUploadDTO.getFile(), 
                    department.getId(),
                    bulkUploadDTO.getDefaultDesignation(),
                    bulkUploadDTO.getDefaultExperienceYears()
            );
            
            response.setTotalRecords(facultyList.size());
            
            int successCount = 0;
            
            for (int i = 0; i < facultyList.size(); i++) {
                FacultyRequestDTO request = facultyList.get(i);
                try {
                    // Validate and add faculty
                    FacultyResponseDTO added = addFaculty(hodId, request);
                    successful.add(added);
                    successCount++;
                } catch (Exception e) {
                    log.error("Error adding faculty at row {}: {}", i + 2, e.getMessage());
                    errors.add(BulkUploadResponseDTO.ErrorRecordDTO.builder()
                            .rowNumber(i + 2) // +2 because Excel rows start at 1 and header is row 1
                            .employeeId(request.getEmployeeId())
                            .email(request.getEmail())
                            .errorMessage(e.getMessage())
                            .rowData(convertRequestToMap(request))
                            .build());
                }
            }
            
            response.setSuccessfulRecords(successCount);
            response.setFailedRecords(facultyList.size() - successCount);
            response.setSuccessfulData(successful.stream()
                    .map(this::convertResponseToMap)
                    .collect(Collectors.toList()));
            response.setErrors(errors);
            response.setStatus(successCount == facultyList.size() ? "SUCCESS" : 
                              successCount > 0 ? "PARTIAL_SUCCESS" : "FAILED");
            response.setMessage(String.format("Uploaded %d out of %d records successfully", 
                    successCount, facultyList.size()));
            
        } catch (Exception e) {
            log.error("Bulk upload failed: {}", e.getMessage());
            response.setStatus("FAILED");
            response.setMessage("Upload failed: " + e.getMessage());
        }
        
        return response;
    }

    @Override
    public byte[] downloadFacultyTemplate() {
        log.info("Downloading faculty upload template");
        return excelHelper.generateFacultyTemplate();
    }

    @Override
    public Page<FacultyResponseDTO> getAllFaculty(Long hodId, String searchTerm, String designation, 
                                                   Role role, Boolean isActive, Pageable pageable) {
        log.info("Fetching faculty list for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        Page<Faculty> facultyPage = facultyRepository.searchFaculty(
                searchTerm, deptId, designation, isActive, pageable);
        
        // Filter by role if specified
        List<Faculty> filtered = facultyPage.getContent().stream()
                .filter(f -> role == null || f.getRoles().contains(role))
                .collect(Collectors.toList());
        
        List<FacultyResponseDTO> dtos = filtered.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, facultyPage.getTotalElements());
    }

    @Override
    public FacultyResponseDTO getFacultyById(Long hodId, Long facultyId) {
        log.info("Fetching faculty by ID: {} for HOD: {}", facultyId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Faculty faculty = getFaculty(facultyId);
        
        // Verify faculty belongs to HOD's department
        if (!faculty.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Faculty does not belong to your department");
        }
        
        return mapToResponseDTO(faculty);
    }

    @Override
    public FacultyResponseDTO updateFaculty(Long hodId, Long facultyId, FacultyRequestDTO request) {
        log.info("Updating faculty ID: {} by HOD: {}", facultyId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Faculty faculty = getFaculty(facultyId);
        
        // Verify faculty belongs to HOD's department
        if (!faculty.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Faculty does not belong to your department");
        }
        
        // Update fields
        faculty.setDesignation(request.getDesignation());
        faculty.setQualification(request.getQualification());
        faculty.setExperienceYears(request.getExperienceYears());
        faculty.setSpecialization(request.getSpecialization());
        faculty.setCabinNumber(request.getCabinNumber());
        faculty.setOfficePhone(request.getOfficePhone());
        faculty.setEmergencyContactName(request.getEmergencyContactName());
        faculty.setEmergencyContactPhone(request.getEmergencyContactPhone());
        faculty.setBloodGroup(request.getBloodGroup());
        faculty.setAddress(request.getAddress());
        faculty.setCity(request.getCity());
        faculty.setState(request.getState());
        faculty.setPincode(request.getPincode());
        
        // Update user fields
        faculty.setFullName(request.getFullName());
        faculty.setPhoneNumber(request.getPhoneNumber());
        
        faculty = facultyRepository.save(faculty);
        
        log.info("Faculty updated successfully: {}", facultyId);
        
        return mapToResponseDTO(faculty);
    }

    @Override
    public void deactivateFaculty(Long hodId, Long facultyId, String reason) {
        log.info("Deactivating faculty ID: {} by HOD: {}, reason: {}", facultyId, hodId, reason);
        
        Faculty hod = getHodFaculty(hodId);
        Faculty faculty = getFaculty(facultyId);
        
        // Verify faculty belongs to HOD's department
        if (!faculty.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Faculty does not belong to your department");
        }
        
        // Cannot deactivate self
        if (facultyId.equals(hodId)) {
            throw new BadRequestException("You cannot deactivate yourself");
        }
        
        faculty.setIsActive(false);
        facultyRepository.save(faculty);
        
        // Update department faculty count
        Department dept = faculty.getDepartment();
        dept.setTotalFaculty(dept.getTotalFaculty() - 1);
        departmentRepository.save(dept);
        
        log.info("Faculty deactivated successfully: {}", facultyId);
    }

    @Override
    public void activateFaculty(Long hodId, Long facultyId) {
        log.info("Activating faculty ID: {} by HOD: {}", facultyId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Faculty faculty = getFaculty(facultyId);
        
        // Verify faculty belongs to HOD's department
        if (!faculty.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Faculty does not belong to your department");
        }
        
        faculty.setIsActive(true);
        facultyRepository.save(faculty);
        
        // Update department faculty count
        Department dept = faculty.getDepartment();
        dept.setTotalFaculty(dept.getTotalFaculty() + 1);
        departmentRepository.save(dept);
        
        log.info("Faculty activated successfully: {}", facultyId);
    }

    @Override
    public FacultyResponseDTO assignRoles(Long hodId, Long facultyId, Set<Role> roles) {
        log.info("Assigning roles to faculty ID: {} by HOD: {}, roles: {}", facultyId, hodId, roles);
        
        Faculty hod = getHodFaculty(hodId);
        Faculty faculty = getFaculty(facultyId);
        
        // Verify faculty belongs to HOD's department
        if (!faculty.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Faculty does not belong to your department");
        }
        
        // Update user roles
        faculty.setRoles(roles);
        facultyRepository.save(faculty);
        
        // Update faculty role flags
        faculty.setIsMentor(roles.contains(Role.FACULTY_MENTOR));
        faculty.setIsClassAdvisor(roles.contains(Role.FACULTY_CLASS_ADVISOR));
        faculty.setIsEventCoordinator(roles.contains(Role.FACULTY_EVENT_COORDINATOR));
        
        faculty = facultyRepository.save(faculty);
        
        log.info("Roles assigned successfully to faculty: {}", facultyId);
        
        return mapToResponseDTO(faculty);
    }

    @Override
    public Map<String, Object> getFacultyStatistics(Long hodId) {
        log.info("Getting faculty statistics for HOD: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalFaculty", facultyRepository.countActiveByDepartment(deptId));
        stats.put("totalMentors", getFacultyCountByRole(deptId, Role.FACULTY_MENTOR));
        stats.put("totalClassAdvisors", getFacultyCountByRole(deptId, Role.FACULTY_CLASS_ADVISOR));
        stats.put("totalEventCoordinators", getFacultyCountByRole(deptId, Role.FACULTY_EVENT_COORDINATOR));
        
        // Distribution by designation
        Map<String, Long> byDesignation = facultyRepository.findByDepartmentId(deptId).stream()
                .filter(Faculty::getIsActive)
                .collect(Collectors.groupingBy(
                        f -> f.getDesignation() != null ? f.getDesignation() : "Not Specified",
                        Collectors.counting()
                ));
        stats.put("byDesignation", byDesignation);
        
        // Distribution by experience
        Map<String, Long> byExperience = facultyRepository.findByDepartmentId(deptId).stream()
                .filter(Faculty::getIsActive)
                .collect(Collectors.groupingBy(
                        f -> {
                            if (f.getExperienceYears() == null) return "Not Specified";
                            if (f.getExperienceYears() < 5) return "0-5 years";
                            if (f.getExperienceYears() < 10) return "5-10 years";
                            return "10+ years";
                        },
                        Collectors.counting()
                ));
        stats.put("byExperience", byExperience);
        
        // Faculty with pending approvals
        List<Object[]> facultyWithPending = facultyRepository.findFacultyWithPendingCount(deptId);
        stats.put("facultyWithPendingApprovals", facultyWithPending);
        
        return stats;
    }

    @Override
    public boolean isEmployeeIdExists(String employeeId) {
        return facultyRepository.existsByEmployeeId(employeeId);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Faculty getHodFaculty(Long hodId) {
        return facultyRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found with ID: " + hodId));
    }

    private Faculty getFaculty(Long facultyId) {
        return facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + facultyId));
    }

    private long getFacultyCountByRole(Long deptId, Role role) {
        return facultyRepository.findByRoleAndDepartment(role, deptId).size();
    }

    private FacultyResponseDTO mapToResponseDTO(Faculty faculty) {
        // Calculate mentees count
        long menteesCount = studentRepository.findByMentorId(faculty.getId()).size();
        
        // Calculate advised class count
        long advisedClassCount = studentRepository.findByClassAdvisorId(faculty.getId()).size();
        
        return FacultyResponseDTO.builder()
                .id(faculty.getId())
                .employeeId(faculty.getEmployeeId())
                .fullName(faculty.getFullName())
                .email(faculty.getEmail())
                .phoneNumber(faculty.getPhoneNumber())
                .profilePicture(faculty.getProfilePicture())
                .departmentId(faculty.getDepartment() != null ? faculty.getDepartment().getId() : null)
                .departmentName(faculty.getDepartment() != null ? faculty.getDepartment().getName() : null)
                .departmentCode(faculty.getDepartment() != null ? faculty.getDepartment().getCode() : null)
                .designation(faculty.getDesignation())
                .qualification(faculty.getQualification())
                .experienceYears(faculty.getExperienceYears())
                .joiningDate(faculty.getJoiningDate())
                .roles(faculty.getRoles())
                .isMentor(faculty.getIsMentor())
                .isClassAdvisor(faculty.getIsClassAdvisor())
                .isEventCoordinator(faculty.getIsEventCoordinator())
                .maxMentees(faculty.getMaxMentees())
                .currentMentees(faculty.getCurrentMentees())
                .menteesCount(menteesCount)
                .advisedClassCount(advisedClassCount)
                .specialization(faculty.getSpecialization())
                .cabinNumber(faculty.getCabinNumber())
                .officePhone(faculty.getOfficePhone())
                .emergencyContactName(faculty.getEmergencyContactName())
                .emergencyContactPhone(faculty.getEmergencyContactPhone())
                .bloodGroup(faculty.getBloodGroup())
                .dateOfBirth(faculty.getDateOfBirth())
                .address(faculty.getAddress())
                .city(faculty.getCity())
                .state(faculty.getState())
                .pincode(faculty.getPincode())
                .isActive(faculty.getIsActive())
                .isFirstLogin(faculty.getIsFirstLogin())
                .lastLoginAt(faculty.getLastLoginAt())
                .createdAt(faculty.getCreatedAt())
                .build();
    }

    private Map<String, Object> convertRequestToMap(FacultyRequestDTO request) {
        Map<String, Object> map = new HashMap<>();
        map.put("employeeId", request.getEmployeeId());
        map.put("fullName", request.getFullName());
        map.put("email", request.getEmail());
        map.put("phoneNumber", request.getPhoneNumber());
        map.put("designation", request.getDesignation());
        map.put("qualification", request.getQualification());
        map.put("experienceYears", request.getExperienceYears());
        return map;
    }

    private Map<String, Object> convertResponseToMap(FacultyResponseDTO response) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", response.getId());
        map.put("employeeId", response.getEmployeeId());
        map.put("fullName", response.getFullName());
        map.put("email", response.getEmail());
        map.put("designation", response.getDesignation());
        return map;
    }
}