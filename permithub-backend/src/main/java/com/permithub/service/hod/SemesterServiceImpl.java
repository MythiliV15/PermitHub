package com.permithub.service.hod;

import com.permithub.dto.hod.SemesterDTO;
import com.permithub.dto.hod.SemesterPromotionDTO;
import com.permithub.entity.*;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import com.permithub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ODRequestRepository odRequestRepository;
    private final OutpassRequestRepository outpassRequestRepository;

    @Override
    public SemesterDTO createSemester(SemesterDTO dto) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Semester semester = Semester.builder()
                .departmentId(deptId)
                .name(dto.getName())
                .year(dto.getYear())
                .semesterNumber(dto.getSemesterNumber())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(false)
                .defaultLeaveBalance(dto.getDefaultLeaveBalance() != null ? dto.getDefaultLeaveBalance() : 20)
                .registrationStartDate(dto.getRegistrationStartDate())
                .registrationEndDate(dto.getRegistrationEndDate())
                .examStartDate(dto.getExamStartDate())
                .examEndDate(dto.getExamEndDate())
                .resultDate(dto.getResultDate())
                .academicYear(dto.getAcademicYear())
                .semesterType(dto.getSemesterType())
                .build();
        semester = semesterRepository.save(semester);
        return mapToDTO(semester);
    }

    @Override
    public SemesterDTO updateSemester(Long semesterId, SemesterDTO dto) {
        Semester s = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        s.setName(dto.getName());
        s.setStartDate(dto.getStartDate());
        s.setEndDate(dto.getEndDate());
        s.setRegistrationStartDate(dto.getRegistrationStartDate());
        s.setRegistrationEndDate(dto.getRegistrationEndDate());
        s.setExamStartDate(dto.getExamStartDate());
        s.setExamEndDate(dto.getExamEndDate());
        s.setResultDate(dto.getResultDate());
        s.setAcademicYear(dto.getAcademicYear());
        s.setSemesterType(dto.getSemesterType());
        return mapToDTO(semesterRepository.save(s));
    }

    @Override
    public Page<SemesterDTO> getAllSemesters(Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<Semester> list = semesterRepository.findByDepartmentId(deptId);
        List<SemesterDTO> dtos = list.stream().map(this::mapToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, list.size());
    }

    @Override
    public SemesterDTO getSemesterById(Long semesterId) {
        Semester s = semesterRepository.findById(semesterId).orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        return mapToDTO(s);
    }

    @Override
    public SemesterDTO getActiveSemester() {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Semester s = semesterRepository.findByDepartmentIdAndIsActiveTrue(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("No active semester found for department"));
        return mapToDTO(s);
    }

    @Override
    public SemesterDTO activateSemester(Long semesterId) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        semesterRepository.deactivateAllByDepartment(deptId);
        Semester s = semesterRepository.findById(semesterId).orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        s.setIsActive(true);
        return mapToDTO(semesterRepository.save(s));
    }

    @Override
    public SemesterDTO deactivateSemester(Long semesterId) {
        Semester s = semesterRepository.findById(semesterId).orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        s.setIsActive(false);
        return mapToDTO(semesterRepository.save(s));
    }

    @Override
    public Map<String, Object> promoteStudents(SemesterPromotionDTO dto) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<StudentProfile> students;
        if (dto.getFromSection() != null) {
            students = studentProfileRepository.findByDepartmentIdAndYearAndSection(deptId, dto.getFromYear(), dto.getFromSection());
        } else {
            students = studentProfileRepository.findByDepartmentIdAndYear(deptId, dto.getFromYear());
        }

        // Exclude specific IDs
        if (dto.getExcludeStudentIds() != null) {
            students = students.stream()
                    .filter(s -> !dto.getExcludeStudentIds().contains(s.getId()))
                    .collect(Collectors.toList());
        }

        for (StudentProfile s : students) {
            // Auto-reject pending requests
            rejectPendingRequestsForStudent(s.getId());
            
            // Promote
            s.setYear(dto.getToYear());
            if (dto.getToSection() != null) s.setSection(dto.getToSection());
            s.setSemesterId(dto.getToSemesterId());
            studentProfileRepository.save(s);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("promotedCount", students.size());
        res.put("status", "SUCCESS");
        return res;
    }

    private void rejectPendingRequestsForStudent(Long studentProfileId) {
        log.info("Auto-rejecting pending requests for student profile: {}", studentProfileId);
        // Note: In Phase 3, we will upgrade Leave/OD/Outpass entities to use profile IDs.
        // For now, this is a placeholder to ensure the logic flow is preserved.
    }

    @Override
    public int resetLeaveBalance(Integer newBalance) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        return studentProfileRepository.resetLeaveBalanceForDepartment(deptId, newBalance != null ? newBalance : 20);
    }

    @Override
    public List<Map<String, Object>> getPromotionEligibility(Integer year, String section) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<StudentProfile> students;
        if (section != null) {
            students = studentProfileRepository.findByDepartmentIdAndYearAndSection(deptId, year, section);
        } else {
            students = studentProfileRepository.findByDepartmentIdAndYear(deptId, year);
        }

        return students.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", s.getId());
                    map.put("regNo", s.getRegNo());
                    map.put("name", s.getName());
                    map.put("isEligible", true);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SemesterDTO setDefaultLeaveLimit(Long semesterId, Integer leaveLimit) {
        Semester s = semesterRepository.findById(semesterId).orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        s.setDefaultLeaveBalance(leaveLimit);
        return mapToDTO(semesterRepository.save(s));
    }

    @Override
    public Map<String, Object> getSemesterStatistics() {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSemesters", semesterRepository.findByDepartmentId(deptId).size());
        return stats;
    }

    private SemesterDTO mapToDTO(Semester s) {
        return SemesterDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .year(s.getYear())
                .semesterNumber(s.getSemesterNumber())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .isActive(s.getIsActive())
                .defaultLeaveBalance(s.getDefaultLeaveBalance())
                .departmentId(s.getDepartmentId())
                .registrationStartDate(s.getRegistrationStartDate())
                .registrationEndDate(s.getRegistrationEndDate())
                .examStartDate(s.getExamStartDate())
                .examEndDate(s.getExamEndDate())
                .resultDate(s.getResultDate())
                .academicYear(s.getAcademicYear())
                .semesterType(s.getSemesterType())
                .build();
    }
}