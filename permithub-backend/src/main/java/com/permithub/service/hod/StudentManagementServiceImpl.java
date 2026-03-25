package com.permithub.service.hod;

import com.permithub.entity.StudentProfile;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.StudentProfileRepository;
import com.permithub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentManagementServiceImpl implements StudentManagementService {

    private final StudentProfileRepository studentProfileRepository;

    @Override
    public Page<StudentProfile> getAllStudents(Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Fetching all students for department ID: {}", deptId);
        List<StudentProfile> students = studentProfileRepository.findByDepartmentId(deptId);
        return new PageImpl<>(students, pageable, students.size());
    }

    @Override
    public Page<StudentProfile> searchStudents(String searchTerm, Integer year, String section, 
                                        Boolean isHosteler, Boolean isActive, Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Searching students in department ID: {} with term: {}", deptId, searchTerm);
        // Simplified search logic using existing repository methods
        List<StudentProfile> students;
        if (year != null && section != null) {
            students = studentProfileRepository.findByDepartmentIdAndYearAndSection(deptId, year, section);
        } else if (year != null) {
            students = studentProfileRepository.findByDepartmentIdAndYear(deptId, year);
        } else {
            students = studentProfileRepository.findByDepartmentId(deptId);
        }
        
        return new PageImpl<>(students, pageable, students.size());
    }

    @Override
    public StudentProfile getStudentById(Long id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    @Override
    @Transactional
    public StudentProfile updateStudentBalance(Long id, Integer balance) {
        StudentProfile student = getStudentById(id);
        student.setLeaveBalance(balance);
        return studentProfileRepository.save(student);
    }

    @Override
    public Map<String, Object> getStudentStats() {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActive", studentProfileRepository.countByDepartmentId(deptId));
        return stats;
    }
}
