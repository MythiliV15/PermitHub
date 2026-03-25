package com.permithub.service.hod;

import com.permithub.dto.hod.BulkUploadResponseDTO;
import com.permithub.dto.hod.FacultyRequestDTO;
import com.permithub.entity.FacultyProfile;
import com.permithub.entity.StudentProfile;
import com.permithub.entity.User;
import com.permithub.exception.BadRequestException;
import com.permithub.repository.FacultyProfileRepository;
import com.permithub.repository.SemesterRepository;
import com.permithub.repository.StudentProfileRepository;
import com.permithub.repository.UserRepository;
import com.permithub.security.SecurityUtils;
import com.permithub.service.EmailService;
import com.permithub.util.ExcelHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BulkUploadServiceImpl implements BulkUploadService {

    private final FacultyProfileRepository facultyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final ExcelHelper excelHelper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String DEFAULT_PASSWORD = "Welcome@123";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    @Override
    public BulkUploadResponseDTO uploadFaculty(MultipartFile file) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new com.permithub.exception.BadRequestException("Could not identify your department for bulk upload. Please re-login.");
        }
        List<FacultyRequestDTO> facultyList = excelHelper.parseFacultyExcel(file);
        List<BulkUploadResponseDTO.ErrorRecordDTO> errors = new ArrayList<>();
        int duplicateCount = 0;
        int successCount = 0;

        // Validate all rows first; if any invalid format is found, abort without inserting.
        for (int i = 0; i < facultyList.size(); i++) {
            FacultyRequestDTO req = facultyList.get(i);
            if (!isValidEmail(req.getEmail())) {
                errors.add(BulkUploadResponseDTO.ErrorRecordDTO.builder()
                        .rowNumber(i + 2)
                        .employeeId(req.getEmployeeId())
                        .email(req.getEmail())
                        .errorMessage("Invalid email")
                        .build());
            }
            if (!isValidPhone(req.getPhone())) {
                errors.add(BulkUploadResponseDTO.ErrorRecordDTO.builder()
                        .rowNumber(i + 2)
                        .employeeId(req.getEmployeeId())
                        .email(req.getEmail())
                        .errorMessage("Invalid phone number")
                        .build());
            }
        }

        if (!errors.isEmpty()) {
            return BulkUploadResponseDTO.builder()
                    .status("FAILED")
                    .totalRecords(facultyList.size())
                    .successfulRecords(0)
                    .failedRecords(facultyList.size())
                    .errors(errors)
                    .message("Upload failed due to invalid email or phone number format")
                    .build();
        }

        for (int i = 0; i < facultyList.size(); i++) {
            FacultyRequestDTO req = facultyList.get(i);
            try {
                if (userRepository.existsByEmail(req.getEmail()) || facultyProfileRepository.existsByEmployeeId(req.getEmployeeId())) {
                    duplicateCount++;
                    errors.add(BulkUploadResponseDTO.ErrorRecordDTO.builder()
                            .rowNumber(i + 2)
                            .employeeId(req.getEmployeeId())
                            .email(req.getEmail())
                            .errorMessage("The details are already uploaded")
                            .build());
                    continue;
                }

                User user = User.builder()
                        .email(req.getEmail())
                        .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                        .role("FACULTY")
                        .departmentId(deptId)
                        .firstLogin(true)
                        .isActive(true)
                        .build();
                user = userRepository.save(user);

                FacultyProfile profile = FacultyProfile.builder()
                        .userId(user.getId())
                        .departmentId(deptId)
                        .name(req.getName())
                        .phone(req.getPhone())
                        .designation(req.getDesignation())
                        .employeeId(req.getEmployeeId())
                        .build();
                facultyProfileRepository.save(profile);
                
                emailService.sendWelcomeEmail(user.getEmail(), profile.getName(), DEFAULT_PASSWORD);
                successCount++;
            } catch (Exception e) {
                log.error("Bulk faculty row error: {}", e.getMessage());
                errors.add(BulkUploadResponseDTO.ErrorRecordDTO.builder()
                        .rowNumber(i + 2)
                        .employeeId(req.getEmployeeId())
                        .email(req.getEmail())
                        .errorMessage("Failed to upload row")
                        .build());
            }
        }

        String status = successCount == facultyList.size() ? "SUCCESS" : (successCount > 0 ? "PARTIAL_SUCCESS" : "FAILED");
        String message;
        if (duplicateCount == facultyList.size() && facultyList.size() > 0) {
            message = "The details are already uploaded";
        } else if ("SUCCESS".equals(status)) {
            message = "Faculty uploaded successfully";
        } else if ("FAILED".equals(status)) {
            message = "Faculty upload failed";
        } else {
            message = "Faculty upload partially completed";
        }

        return BulkUploadResponseDTO.builder()
                .status(status)
                .totalRecords(facultyList.size())
                .successfulRecords(successCount)
                .failedRecords(Math.max(0, facultyList.size() - successCount))
                .errors(errors.isEmpty() ? null : errors)
                .message(message)
                .build();
    }

    @Override
    public BulkUploadResponseDTO uploadStudents(MultipartFile file, Integer year, String section) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Long activeSemesterId = semesterRepository.findByDepartmentIdAndIsActiveTrue(deptId)
                .map(s -> s.getId())
                .orElseThrow(() -> new BadRequestException("No active semester found for your department"));
        List<Map<String, Object>> studentList = excelHelper.parseStudentExcel(file, deptId, year, section);
        int successCount = 0;

        for (Map<String, Object> data : studentList) {
            try {
                String regNo = (String) data.get("registerNumber");
                String email = (String) data.get("email");
                String name = (String) data.get("fullName");

                if (userRepository.existsByEmail(email) || studentProfileRepository.existsByRegNo(regNo)) {
                    continue;
                }

                User user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                        .role("STUDENT")
                        .departmentId(deptId)
                        .firstLogin(true)
                        .isActive(true)
                        .build();
                user = userRepository.save(user);

                StudentProfile profile = StudentProfile.builder()
                        .userId(user.getId())
                        .departmentId(deptId)
                        .semesterId(activeSemesterId)
                        .name(name)
                        .regNo(regNo)
                        .phone((String) data.get("phone"))
                        .year(year)
                        .section(section)
                        .parentName((String) data.get("parentName"))
                        .parentPhone((String) data.get("parentPhone"))
                        .build();
                studentProfileRepository.save(profile);
                
                emailService.sendWelcomeEmail(email, name, DEFAULT_PASSWORD);
                successCount++;
            } catch (Exception e) {
                log.error("Bulk student row error: {}", e.getMessage());
            }
        }
        return BulkUploadResponseDTO.builder()
                .status(successCount == studentList.size() ? "SUCCESS" : "PARTIAL_SUCCESS")
                .totalRecords(studentList.size())
                .successfulRecords(successCount)
                .failedRecords(Math.max(0, studentList.size() - successCount))
                .build();
    }

    @Override
    public byte[] downloadFacultyTemplate() {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Faculty Template");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            String[] cols = {"name", "email", "phone", "designation", "employeeId"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);
            
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (java.io.IOException e) {
            log.error("Failed to generate faculty template: {}", e.getMessage());
            return new byte[0];
        }
    }

    @Override
    public byte[] downloadStudentTemplate() {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Student Template");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            String[] cols = {"fullName", "email", "registerNumber", "parentName", "parentPhone", "isHosteler"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);
            
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (java.io.IOException e) {
            log.error("Failed to generate student template: {}", e.getMessage());
            return new byte[0];
        }
    }

    @Override
    public List<Map<String, Object>> getUploadHistory(int page, int size) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getUploadDetails(Long uploadId) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> validateFile(MultipartFile file, String uploadType) {
        Map<String, Object> res = new HashMap<>();
        res.put("valid", true);
        return res;
    }

    @Override
    public List<Map<String, Object>> previewUpload(MultipartFile file, String uploadType) {
        return new ArrayList<>();
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
}