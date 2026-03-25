package com.permithub.repository;

import com.permithub.entity.FacultyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyProfileRepository extends JpaRepository<FacultyProfile, Long> {
    
    List<FacultyProfile> findByDepartmentId(Long departmentId);
    
    Optional<FacultyProfile> findByUserId(Long userId);
    
    boolean existsByEmployeeId(String employeeId);
}
