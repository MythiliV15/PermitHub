package com.permithub.service.hod;

import com.permithub.entity.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface StudentManagementService {
    Page<StudentProfile> getAllStudents(Pageable pageable);
    
    Page<StudentProfile> searchStudents(String searchTerm, Integer year, String section, 
                                Boolean isHosteler, Boolean isActive, Pageable pageable);
    
    StudentProfile getStudentById(Long id);
    
    StudentProfile updateStudentBalance(Long id, Integer balance);
    
    Map<String, Object> getStudentStats();
}
