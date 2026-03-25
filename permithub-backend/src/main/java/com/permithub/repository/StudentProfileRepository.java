package com.permithub.repository;

import com.permithub.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    
    List<StudentProfile> findByDepartmentId(Long departmentId);
    
    List<StudentProfile> findBySemesterId(Long semesterId);
    
    long countByDepartmentId(Long departmentId);
    
    Optional<StudentProfile> findByUserId(Long userId);
    
    Optional<StudentProfile> findByRegNo(String regNo);
    
    boolean existsByRegNo(String regNo);
    
    List<StudentProfile> findByDepartmentIdAndYear(Long departmentId, Integer year);
    
    List<StudentProfile> findByDepartmentIdAndYearAndSection(Long departmentId, Integer year, String section);
    
    @Modifying
    @Query("UPDATE StudentProfile s SET s.leaveBalance = :newBalance WHERE s.departmentId = :deptId")
    int resetLeaveBalanceForDepartment(Long deptId, Integer newBalance);
}
