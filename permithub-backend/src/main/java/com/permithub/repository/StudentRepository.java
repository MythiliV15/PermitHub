package com.permithub.repository;

import com.permithub.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByRegisterNumber(String registerNumber);
    
    List<Student> findByDepartmentId(Long departmentId);
    
    Page<Student> findByDepartmentId(Long departmentId, Pageable pageable);
    
    List<Student> findByMentorId(Long mentorId);
    
    List<Student> findByClassAdvisorId(Long classAdvisorId);
    
    List<Student> findByYearAndSection(Integer year, String section);
    
    @Query("SELECT s FROM Student s WHERE s.department.id = :deptId AND s.year = :year AND s.section = :section")
    List<Student> findByDepartmentYearAndSection(@Param("deptId") Long deptId, 
                                                 @Param("year") Integer year, 
                                                 @Param("section") String section);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.department.id = :deptId AND s.isActive = true")
    Long countActiveByDepartment(@Param("deptId") Long deptId);
    
    @Query("SELECT s FROM Student s WHERE s.isHosteler = true")
    List<Student> findAllHostelers();
    
    Boolean existsByRegisterNumber(String registerNumber);
    
    // New methods for Phase 2
    
    // Search students with filters
    @Query("SELECT s FROM Student s WHERE " +
           "(:searchTerm IS NULL OR s.fullName LIKE %:searchTerm% OR s.registerNumber LIKE %:searchTerm%) AND " +
           "(:deptId IS NULL OR s.department.id = :deptId) AND " +
           "(:year IS NULL OR s.year = :year) AND " +
           "(:section IS NULL OR s.section = :section) AND " +
           "(:isHosteler IS NULL OR s.isHosteler = :isHosteler) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<Student> searchStudents(
            @Param("searchTerm") String searchTerm,
            @Param("deptId") Long deptId,
            @Param("year") Integer year,
            @Param("section") String section,
            @Param("isHosteler") Boolean isHosteler,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
    
    // Get students grouped by year and section
    @Query("SELECT s.year, s.section, COUNT(s) FROM Student s " +
           "WHERE s.department.id = :deptId AND s.isActive = true " +
           "GROUP BY s.year, s.section ORDER BY s.year, s.section")
    List<Object[]> getStudentDistribution(@Param("deptId") Long deptId);
    
    // Bulk update for semester promotion
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.year = s.year + 1, s.currentSemester = s.currentSemester + 2, " +
           "s.leaveBalance = :newLeaveBalance WHERE s.department.id = :deptId AND s.year = :currentYear")
    int promoteStudents(@Param("deptId") Long deptId, 
                       @Param("currentYear") Integer currentYear,
                       @Param("newLeaveBalance") Integer newLeaveBalance);
    
    // Update leave balance
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.leaveBalance = :newBalance WHERE s.id = :studentId")
    void updateLeaveBalance(@Param("studentId") Long studentId, @Param("newBalance") Integer newBalance);
    
    // Reset leave balance for all students in department
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.leaveBalance = :newBalance WHERE s.department.id = :deptId AND s.isActive = true")
    int resetLeaveBalanceForDepartment(@Param("deptId") Long deptId, @Param("newBalance") Integer newBalance);
}