package com.permithub.repository;

import com.permithub.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    List<Semester> findByDepartmentId(Long departmentId);
    
    Optional<Semester> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    @Query("SELECT s FROM Semester s WHERE s.department.id = :deptId AND s.isActive = true")
    Optional<Semester> findActiveSemesterByDepartment(@Param("deptId") Long deptId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Semester s SET s.isActive = false WHERE s.department.id = :deptId")
    void deactivateAllByDepartment(@Param("deptId") Long deptId);
    
    List<Semester> findByYear(Integer year);
    
    // New methods for Phase 2
    
    @Query("SELECT s FROM Semester s WHERE s.department.id = :deptId ORDER BY s.year DESC, s.semesterNumber DESC")
    List<Semester> findByDepartmentOrderByRecent(@Param("deptId") Long deptId);
    
    @Query("SELECT s FROM Semester s WHERE s.department.id = :deptId AND s.isRegistrationOpen = true")
    Optional<Semester> findSemesterWithOpenRegistration(@Param("deptId") Long deptId);
    
    @Query("SELECT COUNT(s) > 0 FROM Semester s WHERE s.department.id = :deptId AND s.isActive = true")
    boolean hasActiveSemester(@Param("deptId") Long deptId);
    
    @Query("SELECT s FROM Semester s WHERE s.department.id = :deptId AND s.semesterNumber = :semesterNumber AND s.year = :year")
    Optional<Semester> findByDepartmentAndSemesterNumber(
            @Param("deptId") Long deptId, 
            @Param("semesterNumber") Integer semesterNumber,
            @Param("year") Integer year);
}