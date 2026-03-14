package com.permithub.service.hod;

import com.permithub.dto.hod.SemesterDTO;
import com.permithub.dto.hod.SemesterPromotionDTO;
import com.permithub.entity.*;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_LEAVE_BALANCE = 20;

    @Override
    public SemesterDTO createSemester(Long hodId, SemesterDTO semesterDTO) {
        log.info("Creating new semester by HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Department department = hod.getDepartment();
        
        // Validate dates
        if (semesterDTO.getStartDate().isAfter(semesterDTO.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        // Check if semester already exists
        Optional<Semester> existing = semesterRepository.findByDepartmentAndSemesterNumber(
                department.getId(), semesterDTO.getSemesterNumber(), semesterDTO.getYear());
        
        if (existing.isPresent()) {
            throw new BadRequestException("Semester already exists for this year and semester number");
        }
        
        // Create semester
        Semester semester = Semester.builder()
                .name(semesterDTO.getName())
                .year(semesterDTO.getYear())
                .semesterNumber(semesterDTO.getSemesterNumber())
                .startDate(semesterDTO.getStartDate())
                .endDate(semesterDTO.getEndDate())
                .isActive(false)
                .defaultLeaveBalance(semesterDTO.getDefaultLeaveBalance() != null ? 
                        semesterDTO.getDefaultLeaveBalance() : DEFAULT_LEAVE_BALANCE)
                .department(department)
                .registrationStartDate(semesterDTO.getRegistrationStartDate())
                .registrationEndDate(semesterDTO.getRegistrationEndDate())
                .examStartDate(semesterDTO.getExamStartDate())
                .examEndDate(semesterDTO.getExamEndDate())
                .resultDate(semesterDTO.getResultDate())
                .academicYear(semesterDTO.getAcademicYear())
                .semesterType(semesterDTO.getSemesterType())
                .createdBy(hod)
                .isRegistrationOpen(false)
                .isExamPeriod(false)
                .isResultDeclared(false)
                .build();
        
        semester = semesterRepository.save(semester);
        log.info("Semester created successfully with ID: {}", semester.getId());
        
        return mapToDTO(semester);
    }

    @Override
    public SemesterDTO updateSemester(Long hodId, Long semesterId, SemesterDTO semesterDTO) {
        log.info("Updating semester ID: {} by HOD: {}", semesterId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Semester semester = getSemester(semesterId);
        
        // Verify semester belongs to HOD's department
        if (!semester.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Semester does not belong to your department");
        }
        
        // Update fields
        semester.setName(semesterDTO.getName());
        semester.setStartDate(semesterDTO.getStartDate());
        semester.setEndDate(semesterDTO.getEndDate());
        semester.setRegistrationStartDate(semesterDTO.getRegistrationStartDate());
        semester.setRegistrationEndDate(semesterDTO.getRegistrationEndDate());
        semester.setExamStartDate(semesterDTO.getExamStartDate());
        semester.setExamEndDate(semesterDTO.getExamEndDate());
        semester.setResultDate(semesterDTO.getResultDate());
        semester.setAcademicYear(semesterDTO.getAcademicYear());
        semester.setSemesterType(semesterDTO.getSemesterType());
        
        semester = semesterRepository.save(semester);
        log.info("Semester updated successfully: {}", semesterId);
        
        return mapToDTO(semester);
    }

    @Override
    public Page<SemesterDTO> getAllSemesters(Long hodId, Pageable pageable) {
        log.info("Fetching all semesters for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Semester> semesters = semesterRepository.findByDepartmentOrderByRecent(deptId);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), semesters.size());
        
        List<SemesterDTO> dtos = semesters.subList(start, end).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, semesters.size());
    }

    @Override
    public SemesterDTO getSemesterById(Long hodId, Long semesterId) {
        log.info("Fetching semester ID: {} for HOD: {}", semesterId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Semester semester = getSemester(semesterId);
        
        // Verify semester belongs to HOD's department
        if (!semester.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Semester does not belong to your department");
        }
        
        return mapToDTO(semester);
    }

    @Override
    public SemesterDTO getActiveSemester(Long hodId) {
        log.info("Fetching active semester for HOD: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        Semester active = semesterRepository.findActiveSemesterByDepartment(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("No active semester found for your department"));
        
        return mapToDTO(active);
    }

    @Override
    public SemesterDTO activateSemester(Long hodId, Long semesterId) {
        log.info("Activating semester ID: {} by HOD: {}", semesterId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Semester semester = getSemester(semesterId);
        Long deptId = hod.getDepartment().getId();
        
        // Verify semester belongs to HOD's department
        if (!semester.getDepartment().getId().equals(deptId)) {
            throw new BadRequestException("Semester does not belong to your department");
        }
        
        // Deactivate all other semesters
        semesterRepository.deactivateAllByDepartment(deptId);
        
        // Activate this semester
        semester.setIsActive(true);
        semester = semesterRepository.save(semester);
        
        log.info("Semester activated successfully: {}", semesterId);
        
        return mapToDTO(semester);
    }

    @Override
    public SemesterDTO deactivateSemester(Long hodId, Long semesterId) {
        log.info("Deactivating semester ID: {} by HOD: {}", semesterId, hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Semester semester = getSemester(semesterId);
        
        // Verify semester belongs to HOD's department
        if (!semester.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Semester does not belong to your department");
        }
        
        semester.setIsActive(false);
        semester = semesterRepository.save(semester);
        
        log.info("Semester deactivated successfully: {}", semesterId);
        
        return mapToDTO(semester);
    }

    @Override
    public Map<String, Object> promoteStudents(Long hodId, SemesterPromotionDTO promotionDTO) {
        log.info("Promoting students by HOD: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        // Validate department
        if (!deptId.equals(promotionDTO.getDepartmentId())) {
            throw new BadRequestException("Department mismatch");
        }
        
        // Get new semester
        Semester newSemester = getSemester(promotionDTO.getNewSemesterId());
        if (!newSemester.getDepartment().getId().equals(deptId)) {
            throw new BadRequestException("New semester does not belong to your department");
        }
        
        // Get students to promote
        List<Student> studentsToPromote;
        if (promotionDTO.getFromSection() != null) {
            studentsToPromote = studentRepository.findByDepartmentYearAndSection(
                    deptId, promotionDTO.getFromYear(), promotionDTO.getFromSection());
        } else {
            studentsToPromote = studentRepository.findByDepartmentId(deptId).stream()
                    .filter(s -> s.getYear().equals(promotionDTO.getFromYear()) && s.getIsActive())
                    .collect(Collectors.toList());
        }
        
        // Filter out excluded students
        if (promotionDTO.getExcludeStudentIds() != null && !promotionDTO.getExcludeStudentIds().isEmpty()) {
            studentsToPromote = studentsToPromote.stream()
                    .filter(s -> !promotionDTO.getExcludeStudentIds().contains(s.getId()))
                    .collect(Collectors.toList());
        }
        
        // Check eligibility if required
        List<Student> eligibleStudents = studentsToPromote;
        if (promotionDTO.getPromoteOnlyPassedStudents()) {
            eligibleStudents = studentsToPromote.stream()
                    .filter(this::isEligibleForPromotion)
                    .collect(Collectors.toList());
        }
        
        // Auto-reject pending requests
        rejectPendingRequests(eligibleStudents);
        
        // Perform promotion
        int promotedCount = 0;
        List<String> promotedStudents = new ArrayList<>();
        
        for (Student student : eligibleStudents) {
            try {
                // Update student
                student.setYear(promotionDTO.getToYear());
                if (promotionDTO.getToSection() != null) {
                    student.setSection(promotionDTO.getToSection());
                }
                student.setCurrentSemester(student.getCurrentSemester() + 2);
                student.setLeaveBalance(promotionDTO.getNewLeaveBalance() != null ? 
                        promotionDTO.getNewLeaveBalance() : DEFAULT_LEAVE_BALANCE);
                
                studentRepository.save(student);
                promotedCount++;
                promotedStudents.add(student.getFullName() + " (" + student.getRegisterNumber() + ")");
                
                log.info("Student promoted: {} - {}", student.getRegisterNumber(), student.getFullName());
            } catch (Exception e) {
                log.error("Failed to promote student: {}", student.getRegisterNumber(), e);
            }
        }
        
        // Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("totalStudents", studentsToPromote.size());
        result.put("eligibleStudents", eligibleStudents.size());
        result.put("promotedCount", promotedCount);
        result.put("failedCount", eligibleStudents.size() - promotedCount);
        result.put("promotedStudents", promotedStudents);
        result.put("newSemester", newSemester.getName());
        result.put("promotionDate", LocalDate.now());
        result.put("remarks", promotionDTO.getRemarks());
        
        log.info("Promotion completed: {}/{} students promoted", promotedCount, eligibleStudents.size());
        
        return result;
    }

    @Override
    public int resetLeaveBalance(Long hodId, Integer newBalance) {
        log.info("Resetting leave balance for department by HOD: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        int resetCount = studentRepository.resetLeaveBalanceForDepartment(
                deptId, newBalance != null ? newBalance : DEFAULT_LEAVE_BALANCE);
        
        log.info("Leave balance reset for {} students", resetCount);
        
        return resetCount;
    }

    @Override
    public List<Map<String, Object>> getPromotionEligibility(Long hodId, Integer year, String section) {
        log.info("Getting promotion eligibility for HOD: {}, year: {}, section: {}", hodId, year, section);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Student> students;
        if (section != null) {
            students = studentRepository.findByDepartmentYearAndSection(deptId, year, section);
        } else {
            students = studentRepository.findByDepartmentId(deptId).stream()
                    .filter(s -> s.getYear().equals(year) && s.getIsActive())
                    .collect(Collectors.toList());
        }
        
        List<Map<String, Object>> eligibilityList = new ArrayList<>();
        
        for (Student student : students) {
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("studentId", student.getId());
            studentInfo.put("registerNumber", student.getRegisterNumber());
            studentInfo.put("name", student.getFullName());
            studentInfo.put("currentYear", student.getYear());
            studentInfo.put("currentSection", student.getSection());
            studentInfo.put("isEligible", isEligibleForPromotion(student));
            
            // Check for pending requests
            boolean hasPendingLeaves = leaveRequestRepository.findByStudent(student).stream()
                    .anyMatch(lr -> lr.getStatus().toString().contains("PENDING"));
            studentInfo.put("hasPendingRequests", hasPendingLeaves);
            
            // Attendance (would need attendance repository)
            studentInfo.put("attendance", "75%"); // Placeholder
            
            // Arrear status
            studentInfo.put("hasArrears", false); // Placeholder
            
            eligibilityList.add(studentInfo);
        }
        
        return eligibilityList;
    }

    @Override
    public SemesterDTO setDefaultLeaveLimit(Long hodId, Long semesterId, Integer leaveLimit) {
        log.info("Setting default leave limit to {} for semester: {}", leaveLimit, semesterId);
        
        Faculty hod = getHodFaculty(hodId);
        Semester semester = getSemester(semesterId);
        
        // Verify semester belongs to HOD's department
        if (!semester.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Semester does not belong to your department");
        }
        
        semester.setDefaultLeaveBalance(leaveLimit);
        semester = semesterRepository.save(semester);
        
        log.info("Default leave limit updated for semester: {}", semesterId);
        
        return mapToDTO(semester);
    }

    @Override
    public Map<String, Object> getSemesterStatistics(Long hodId) {
        log.info("Getting semester statistics for HOD: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Semester> semesters = semesterRepository.findByDepartmentOrderByRecent(deptId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSemesters", semesters.size());
        stats.put("activeSemester", semesters.stream().filter(Semester::getIsActive).findFirst()
                .map(Semester::getName).orElse("None"));
        
        // Semester-wise student distribution
        Map<String, Long> studentsPerSemester = new HashMap<>();
        for (Student student : studentRepository.findByDepartmentId(deptId)) {
            String semesterKey = "Sem " + student.getCurrentSemester();
            studentsPerSemester.put(semesterKey, studentsPerSemester.getOrDefault(semesterKey, 0L) + 1);
        }
        stats.put("studentsPerSemester", studentsPerSemester);
        
        // Upcoming semesters
        List<String> upcomingSemesters = semesters.stream()
                .filter(s -> !s.getIsActive() && s.getStartDate().isAfter(LocalDate.now()))
                .map(Semester::getName)
                .collect(Collectors.toList());
        stats.put("upcomingSemesters", upcomingSemesters);
        
        return stats;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Faculty getHodFaculty(Long hodId) {
        return facultyRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found with ID: " + hodId));
    }

    private Semester getSemester(Long semesterId) {
        return semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with ID: " + semesterId));
    }

    private boolean isEligibleForPromotion(Student student) {
        // Check if student has any pending requests
        boolean hasPendingLeaves = leaveRequestRepository.findByStudent(student).stream()
                .anyMatch(lr -> lr.getStatus().toString().contains("PENDING"));
        
        // Check attendance (placeholder - would need actual attendance data)
        // Check arrears (placeholder - would need exam results)
        
        return !hasPendingLeaves; // Simplified for now
    }

    private void rejectPendingRequests(List<Student> students) {
        for (Student student : students) {
            // Reject pending leave requests
            leaveRequestRepository.findByStudent(student).stream()
                    .filter(lr -> lr.getStatus() == RequestStatus.PENDING)
                    .forEach(lr -> {
                        lr.setStatus(RequestStatus.REJECTED_BY_HOD);
                        lr.setHodRemark("Auto-rejected due to semester promotion");
                        leaveRequestRepository.save(lr);
                    });
            
            // Similar for OD and outpass requests would go here
        }
    }

    private SemesterDTO mapToDTO(Semester semester) {
        return SemesterDTO.builder()
                .id(semester.getId())
                .name(semester.getName())
                .year(semester.getYear())
                .semesterNumber(semester.getSemesterNumber())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .isActive(semester.getIsActive())
                .defaultLeaveBalance(semester.getDefaultLeaveBalance())
                .departmentId(semester.getDepartment() != null ? semester.getDepartment().getId() : null)
                .registrationStartDate(semester.getRegistrationStartDate())
                .registrationEndDate(semester.getRegistrationEndDate())
                .examStartDate(semester.getExamStartDate())
                .examEndDate(semester.getExamEndDate())
                .resultDate(semester.getResultDate())
                .academicYear(semester.getAcademicYear())
                .semesterType(semester.getSemesterType())
                .isRegistrationOpen(semester.getIsRegistrationOpen())
                .isExamPeriod(semester.getIsExamPeriod())
                .isResultDeclared(semester.getIsResultDeclared())
                .createdById(semester.getCreatedBy() != null ? semester.getCreatedBy().getId() : null)
                .createdByName(semester.getCreatedBy() != null ? semester.getCreatedBy().getFullName() : null)
                .build();
    }
}