package com.permithub.service.hod;
import com.permithub.service.EmailService;

import com.permithub.dto.hod.BulkUploadResponseDTO;
import com.permithub.dto.hod.FacultyRequestDTO;
import com.permithub.entity.*;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import com.permithub.util.ExcelHelper;
import com.permithub.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BulkUploadServiceImpl implements BulkUploadService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ExcelHelper excelHelper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final EmailService emailService;
    
    // We'll create this repository later if needed
    // private final BulkUploadHistoryRepository uploadHistoryRepository;

    private static final String DEFAULT_PASSWORD = "Welcome@123";
    private static final int BATCH_SIZE = 50;

    @Override
    public BulkUploadResponseDTO uploadFaculty(Long hodId, MultipartFile file, Long departmentId) {
        log.info("Starting bulk faculty upload by HOD ID: {}, department: {}", hodId, departmentId);
        
        long startTime = System.currentTimeMillis();
        
        // Validate HOD
        Faculty hod = getHodFaculty(hodId);
        Department department = getDepartment(departmentId);
        
        // Verify department belongs to HOD
        if (!department.getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("You can only upload faculty to your own department");
        }
        
        BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                .uploadType("FACULTY")
                .fileName(file.getOriginalFilename())
                .status("PROCESSING")
                .build();
        
        List<BulkUploadResponseDTO.ErrorRecordDTO> errors = new ArrayList<>();
        List<Map<String, Object>> successfulData = new ArrayList<>();
        
        try {
            // Parse Excel file
            List<FacultyRequestDTO> facultyList = excelHelper.parseFacultyExcel(file, departmentId, null, null);
            
            response.setTotalRecords(facultyList.size());
            
            AtomicInteger successCount = new AtomicInteger(0);
            int rowNum = 2; // Start from row 2 (after header)
            
            for (FacultyRequestDTO request : facultyList) {
                try {
                    // Process each faculty
                    Map<String, Object> result = processSingleFaculty(request, department, hod, rowNum);
                    if (result.containsKey("success") && (Boolean) result.get("success")) {
                        successCount.incrementAndGet();
                        successfulData.add(result);
                    } else {
                        errors.add(createErrorRecord(rowNum, request, (String) result.get("error")));
                    }
                } catch (Exception e) {
                    log.error("Error processing faculty at row {}: {}", rowNum, e.getMessage());
                    errors.add(createErrorRecord(rowNum, request, e.getMessage()));
                }
                rowNum++;
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            response.setSuccessfulRecords(successCount.get());
            response.setFailedRecords(facultyList.size() - successCount.get());
            response.setSuccessfulData(successfulData);
            response.setErrors(errors);
            response.setStatus(determineStatus(successCount.get(), facultyList.size()));
            response.setMessage(generateSummaryMessage(successCount.get(), facultyList.size()));
            response.setProcessingTimeMs(processingTime);
            
            // Save upload history (commented out until repository is created)
            // saveUploadHistory(response, hod, department, file);
            
            log.info("Bulk faculty upload completed: {}/{} successful in {}ms", 
                    successCount.get(), facultyList.size(), processingTime);
            
        } catch (Exception e) {
            log.error("Bulk upload failed: {}", e.getMessage());
            response.setStatus("FAILED");
            response.setMessage("Upload failed: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        }
        
        return response;
    }

    @Override
    public BulkUploadResponseDTO uploadStudents(Long hodId, MultipartFile file, Long departmentId, 
                                                Integer year, String section) {
        log.info("Starting bulk student upload by HOD ID: {}, department: {}, year: {}, section: {}", 
                hodId, departmentId, year, section);
        
        long startTime = System.currentTimeMillis();
        
        // Validate HOD
        Faculty hod = getHodFaculty(hodId);
        Department department = getDepartment(departmentId);
        
        // Verify department belongs to HOD
        if (!department.getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("You can only upload students to your own department");
        }
        
        BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                .uploadType("STUDENT")
                .fileName(file.getOriginalFilename())
                .status("PROCESSING")
                .build();
        
        List<BulkUploadResponseDTO.ErrorRecordDTO> errors = new ArrayList<>();
        List<Map<String, Object>> successfulData = new ArrayList<>();
        
        try {
            // Parse Excel file for students
            List<Map<String, Object>> studentList = excelHelper.parseStudentExcel(file, departmentId, year, section);
            
            response.setTotalRecords(studentList.size());
            
            AtomicInteger successCount = new AtomicInteger(0);
            int rowNum = 2; // Start from row 2 (after header)
            
            for (Map<String, Object> studentData : studentList) {
                try {
                    // Process each student
                    Map<String, Object> result = processSingleStudent(studentData, department, year, section, hod, rowNum);
                    if (result.containsKey("success") && (Boolean) result.get("success")) {
                        successCount.incrementAndGet();
                        successfulData.add(result);
                    } else {
                        errors.add(createStudentErrorRecord(rowNum, studentData, (String) result.get("error")));
                    }
                } catch (Exception e) {
                    log.error("Error processing student at row {}: {}", rowNum, e.getMessage());
                    errors.add(createStudentErrorRecord(rowNum, studentData, e.getMessage()));
                }
                rowNum++;
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            response.setSuccessfulRecords(successCount.get());
            response.setFailedRecords(studentList.size() - successCount.get());
            response.setSuccessfulData(successfulData);
            response.setErrors(errors);
            response.setStatus(determineStatus(successCount.get(), studentList.size()));
            response.setMessage(generateSummaryMessage(successCount.get(), studentList.size()));
            response.setProcessingTimeMs(processingTime);
            
            log.info("Bulk student upload completed: {}/{} successful in {}ms", 
                    successCount.get(), studentList.size(), processingTime);
            
        } catch (Exception e) {
            log.error("Bulk upload failed: {}", e.getMessage());
            response.setStatus("FAILED");
            response.setMessage("Upload failed: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        }
        
        return response;
    }

    @Override
    public byte[] downloadFacultyTemplate() {
        log.info("Downloading faculty upload template");
        return excelHelper.generateFacultyTemplate();
    }

    @Override
    public byte[] downloadStudentTemplate() {
        log.info("Downloading student upload template");
        return excelHelper.generateStudentTemplate();
    }

    @Override
    public List<Map<String, Object>> getUploadHistory(Long hodId, int page, int size) {
        log.info("Fetching upload history for HOD ID: {}, page: {}, size: {}", hodId, page, size);
        
        // This would be implemented when BulkUploadHistory repository is created
        // For now, return empty list
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getUploadDetails(Long hodId, Long uploadId) {
        log.info("Fetching upload details for ID: {}", uploadId);
        
        // This would be implemented when BulkUploadHistory repository is created
        // For now, return empty map
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Upload history feature will be implemented in Phase 3");
        return details;
    }

    @Override
    public Map<String, Object> validateFile(MultipartFile file, String uploadType) {
        log.info("Validating file: {}, type: {}", file.getOriginalFilename(), uploadType);
        
        Map<String, Object> validationResult = new HashMap<>();
        List<String> errors = new ArrayList<>();
        
        // Check if file is empty
        if (file.isEmpty()) {
            errors.add("File is empty");
            validationResult.put("valid", false);
            validationResult.put("errors", errors);
            return validationResult;
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
            !contentType.equals("application/vnd.ms-excel")) {
            errors.add("Invalid file type. Please upload an Excel file (.xlsx or .xls)");
        }
        
        // Check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            errors.add("File size exceeds 10MB limit");
        }
        
        // Validate structure
        try {
            if ("FACULTY".equalsIgnoreCase(uploadType)) {
                excelHelper.validateFacultyExcel(file);
            } else if ("STUDENT".equalsIgnoreCase(uploadType)) {
                excelHelper.validateStudentExcel(file);
            } else {
                errors.add("Invalid upload type");
            }
        } catch (Exception e) {
            errors.add("File validation failed: " + e.getMessage());
        }
        
        validationResult.put("valid", errors.isEmpty());
        validationResult.put("errors", errors);
        validationResult.put("fileName", file.getOriginalFilename());
        validationResult.put("fileSize", file.getSize());
        validationResult.put("uploadType", uploadType);
        
        return validationResult;
    }

    @Override
    public List<Map<String, Object>> previewUpload(MultipartFile file, String uploadType) {
        log.info("Previewing upload file: {}, type: {}", file.getOriginalFilename(), uploadType);
        
        try {
            if ("FACULTY".equalsIgnoreCase(uploadType)) {
                return excelHelper.previewFacultyExcel(file);
            } else if ("STUDENT".equalsIgnoreCase(uploadType)) {
                return excelHelper.previewStudentExcel(file);
            } else {
                throw new BadRequestException("Invalid upload type");
            }
        } catch (Exception e) {
            log.error("Error previewing file: {}", e.getMessage());
            throw new BadRequestException("Failed to preview file: " + e.getMessage());
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Faculty getHodFaculty(Long hodId) {
        return facultyRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found with ID: " + hodId));
    }

    private Department getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
    }

    private Map<String, Object> processSingleFaculty(FacultyRequestDTO request, Department department, 
                                                      Faculty hod, int rowNum) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check for existing employee ID
            if (facultyRepository.existsByEmployeeId(request.getEmployeeId())) {
                throw new BadRequestException("Employee ID already exists: " + request.getEmployeeId());
            }
            
            // Check for existing email
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists: " + request.getEmail());
            }
            
            // Generate password
            String password = passwordGenerator.generatePasswordFromId(request.getEmployeeId());
            String encryptedPassword = passwordEncoder.encode(password);
            
            // Create faculty directly
            Faculty faculty = Faculty.builder()
                    .email(request.getEmail())
                    .password(encryptedPassword)
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
                    .maxMentees(20)
                    .currentMentees(0)
                    .specialization(request.getSpecialization())
                    .cabinNumber(request.getCabinNumber())
                    .officePhone(request.getOfficePhone())
                    .build();
            
            faculty = facultyRepository.save(faculty);
            
            // Update department faculty count
            department.setTotalFaculty(department.getTotalFaculty() + 1);
            departmentRepository.save(department);
            
            // Send welcome email (async)
            try {
                emailService.sendWelcomeEmail(request.getEmail(), request.getFullName(), password);
            } catch (Exception e) {
                log.warn("Failed to send welcome email to: {}", request.getEmail());
            }
            
            // Prepare success result
            result.put("success", true);
            result.put("id", faculty.getId());
            result.put("employeeId", faculty.getEmployeeId());
            result.put("name", faculty.getFullName());
            result.put("email", faculty.getEmail());
            result.put("password", password); // Only for response, remove in production
            result.put("message", "Faculty added successfully");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    private Map<String, Object> processSingleStudent(Map<String, Object> studentData, Department department,
                                                       Integer year, String section, Faculty hod, int rowNum) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String registerNumber = (String) studentData.get("registerNumber");
            String email = (String) studentData.get("email");
            String fullName = (String) studentData.get("fullName");
            
            // Check for existing register number
            if (studentRepository.existsByRegisterNumber(registerNumber)) {
                throw new BadRequestException("Register number already exists: " + registerNumber);
            }
            
            // Check for existing email
            if (userRepository.existsByEmail(email)) {
                throw new BadRequestException("Email already exists: " + email);
            }
            
            // Generate password
            String password = passwordGenerator.generatePasswordFromId(registerNumber);
            String encryptedPassword = passwordEncoder.encode(password);
            
            // Create student directly
            Student student = Student.builder()
                    .email(email)
                    .password(encryptedPassword)
                    .fullName(fullName)
                    .phoneNumber((String) studentData.get("phoneNumber"))
                    .isFirstLogin(true)
                    .emailVerified(false)
                    .isActive(true)
                    .roles(new HashSet<>(Collections.singletonList(Role.STUDENT)))
                    .registerNumber(registerNumber)
                    .department(department)
                    .year(year)
                    .section(section)
                    .isHosteler(studentData.get("isHosteler") != null ? 
                            Boolean.parseBoolean(studentData.get("isHosteler").toString()) : false)
                    .parentName((String) studentData.get("parentName"))
                    .parentPhone((String) studentData.get("parentPhone"))
                    .parentEmail((String) studentData.get("parentEmail"))
                    .emergencyContact((String) studentData.get("emergencyContact"))
                    .batch(year + "-" + (year + 4))
                    .admissionYear(year)
                    .currentSemester(calculateSemester(year))
                    .leaveBalance(20)
                    .dateOfBirth(studentData.get("dateOfBirth") != null ? 
                            java.time.LocalDate.parse(studentData.get("dateOfBirth").toString()) : null)
                    .address((String) studentData.get("address"))
                    .build();
            
            student = studentRepository.save(student);
            
            // Update department student count
            department.setTotalStudents(department.getTotalStudents() + 1);
            departmentRepository.save(department);
            
            // Send welcome email (async)
            try {
                emailService.sendWelcomeEmail(email, fullName, password);
            } catch (Exception e) {
                log.warn("Failed to send welcome email to: {}", email);
            }
            
            // Prepare success result
            result.put("success", true);
            result.put("id", student.getId());
            result.put("registerNumber", student.getRegisterNumber());
            result.put("name", student.getFullName());
            result.put("email", student.getEmail());
            result.put("password", password); // Only for response, remove in production
            result.put("message", "Student added successfully");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    private Integer calculateSemester(Integer year) {
        // Assuming current year is 2024, calculate semester based on admission year
        int currentYear = java.time.Year.now().getValue();
        int yearsPassed = currentYear - year;
        return (yearsPassed * 2) + 1; // 1st year: sem 1-2, 2nd year: sem 3-4, etc.
    }

    private BulkUploadResponseDTO.ErrorRecordDTO createErrorRecord(int rowNum, FacultyRequestDTO request, String error) {
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("employeeId", request.getEmployeeId());
        rowData.put("name", request.getFullName());
        rowData.put("email", request.getEmail());
        
        return BulkUploadResponseDTO.ErrorRecordDTO.builder()
                .rowNumber(rowNum)
                .employeeId(request.getEmployeeId())
                .email(request.getEmail())
                .errorMessage(error)
                .rowData(rowData)
                .build();
    }

    private BulkUploadResponseDTO.ErrorRecordDTO createStudentErrorRecord(int rowNum, Map<String, Object> studentData, String error) {
        return BulkUploadResponseDTO.ErrorRecordDTO.builder()
                .rowNumber(rowNum)
                .employeeId((String) studentData.get("registerNumber"))
                .email((String) studentData.get("email"))
                .errorMessage(error)
                .rowData(studentData)
                .build();
    }

    private String determineStatus(int successCount, int totalCount) {
        if (successCount == 0) {
            return "FAILED";
        } else if (successCount == totalCount) {
            return "SUCCESS";
        } else {
            return "PARTIAL_SUCCESS";
        }
    }

    private String generateSummaryMessage(int successCount, int totalCount) {
        if (successCount == 0) {
            return "Upload failed. No records were inserted.";
        } else if (successCount == totalCount) {
            return String.format("Successfully uploaded all %d records.", totalCount);
        } else {
            return String.format("Partially successful: %d out of %d records uploaded. %d records failed.",
                    successCount, totalCount, (totalCount - successCount));
        }
    }

    // This will be implemented when BulkUploadHistory repository is created
    /*
    private void saveUploadHistory(BulkUploadResponseDTO response, Faculty hod, Department department, MultipartFile file) {
        BulkUploadHistory history = BulkUploadHistory.builder()
                .uploadType(response.getUploadType())
                .fileName(response.getFileName())
                .filePath(null) // Would need file storage implementation
                .uploadedBy(hod.getUser())
                .department(department)
                .uploadedDate(LocalDateTime.now())
                .totalRecords(response.getTotalRecords())
                .successfulRecords(response.getSuccessfulRecords())
                .failedRecords(response.getFailedRecords())
                .errorLog(convertErrorsToJson(response.getErrors()))
                .status(response.getStatus())
                .processingTimeMs(response.getProcessingTimeMs())
                .fileSizeBytes(file.getSize())
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .build();
        
        uploadHistoryRepository.save(history);
    }

    private String convertErrorsToJson(List<BulkUploadResponseDTO.ErrorRecordDTO> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }
        try {
            return new ObjectMapper().writeValueAsString(errors);
        } catch (Exception e) {
            log.error("Failed to convert errors to JSON", e);
            return null;
        }
    }
    */
}