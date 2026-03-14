package com.permithub.repository;

import com.permithub.entity.Faculty;
import com.permithub.entity.Role;
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
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    
    Optional<Faculty> findByEmployeeId(String employeeId);
    
    List<Faculty> findByDepartmentId(Long departmentId);
    
    Page<Faculty> findByDepartmentId(Long departmentId, Pageable pageable);
    
    @Query("SELECT f FROM Faculty f WHERE :eventType MEMBER OF f.coordinatedEventTypes")
    List<Faculty> findByCoordinatedEventType(@Param("eventType") String eventType);
    
    @Query("SELECT f FROM Faculty f WHERE SIZE(f.mentees) > 0")
    List<Faculty> findAllMentors();
    
    @Query("SELECT f FROM Faculty f WHERE SIZE(f.advisedClass) > 0")
    List<Faculty> findAllClassAdvisors();
    
    Boolean existsByEmployeeId(String employeeId);
    
    // New methods for Phase 2
    
    // Find faculty by role
    @Query("SELECT DISTINCT u FROM Faculty u JOIN u.roles r WHERE r = :role AND u.department.id = :deptId")
    List<Faculty> findByRoleAndDepartment(@Param("role") Role role, @Param("deptId") Long deptId);
    
    // Search faculty with filters
    @Query("SELECT f FROM Faculty f WHERE " +
           "(:searchTerm IS NULL OR f.fullName LIKE %:searchTerm% OR f.employeeId LIKE %:searchTerm% OR f.email LIKE %:searchTerm%) AND " +
           "(:deptId IS NULL OR f.department.id = :deptId) AND " +
           "(:designation IS NULL OR f.designation = :designation) AND " +
           "(:isActive IS NULL OR f.isActive = :isActive)")
    Page<Faculty> searchFaculty(
            @Param("searchTerm") String searchTerm,
            @Param("deptId") Long deptId,
            @Param("designation") String designation,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
    
    // Count faculty by department
    @Query("SELECT COUNT(f) FROM Faculty f WHERE f.department.id = :deptId AND f.isActive = true")
    long countActiveByDepartment(@Param("deptId") Long deptId);
    
    // Get faculty with pending approvals count
    @Query("SELECT f, COUNT(lr) FROM Faculty f " +
           "LEFT JOIN LeaveRequest lr ON lr.mentor = f AND lr.status = com.permithub.entity.RequestStatus.PENDING " +
           "WHERE f.department.id = :deptId GROUP BY f.id")
    List<Object[]> findFacultyWithPendingCount(@Param("deptId") Long deptId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Faculty f SET f.isMentor = :isMentor, f.isClassAdvisor = :isClassAdvisor, " +
           "f.isEventCoordinator = :isEventCoordinator WHERE f.id = :facultyId")
    void updateFacultyRoles(@Param("facultyId") Long facultyId,
                           @Param("isMentor") Boolean isMentor,
                           @Param("isClassAdvisor") Boolean isClassAdvisor,
                           @Param("isEventCoordinator") Boolean isEventCoordinator);
    
    @Modifying
    @Transactional
    @Query("UPDATE Faculty f SET f.currentMentees = f.currentMentees + 1 WHERE f.id = :facultyId")
    void incrementMenteeCount(@Param("facultyId") Long facultyId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Faculty f SET f.currentMentees = f.currentMentees - 1 WHERE f.id = :facultyId")
    void decrementMenteeCount(@Param("facultyId") Long facultyId);
}