package com.permithub.repository;

import com.permithub.entity.LeaveRequest;
import com.permithub.entity.RequestStatus;
import com.permithub.entity.Student;
import com.permithub.entity.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    // Find by student
    List<LeaveRequest> findByStudent(Student student);
    
    Page<LeaveRequest> findByStudent(Student student, Pageable pageable);
    
    // Find by status
    List<LeaveRequest> findByStatus(RequestStatus status);
    
    // Find pending approvals for HOD (after class advisor approval)
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'APPROVED_BY_CLASS_ADVISOR' AND lr.student.department.id = :deptId")
    List<LeaveRequest> findPendingHODApprovals(@Param("deptId") Long deptId);
    
    // Find by date range
    List<LeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end);
    
    // Count by status for a department
    @Query("SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.status = :status AND lr.student.department.id = :deptId")
    long countByStatusAndDepartment(@Param("status") RequestStatus status, @Param("deptId") Long deptId);
    
    // Find by mentor
    List<LeaveRequest> findByMentor(Faculty mentor);
    
    // Find by class advisor
    List<LeaveRequest> findByClassAdvisor(Faculty classAdvisor);
    
    // Find by HOD
    List<LeaveRequest> findByHod(Faculty hod);
    
    // Search with filters
    @Query("SELECT lr FROM LeaveRequest lr WHERE " +
           "(:studentName IS NULL OR lr.student.fullName LIKE %:studentName%) AND " +
           "(:status IS NULL OR lr.status = :status) AND " +
           "(:deptId IS NULL OR lr.student.department.id = :deptId) AND " +
           "(:startDate IS NULL OR lr.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR lr.endDate <= :endDate)")
    Page<LeaveRequest> searchLeaves(
            @Param("studentName") String studentName,
            @Param("status") RequestStatus status,
            @Param("deptId") Long deptId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // Get recent leaves for a department
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.student.department.id = :deptId ORDER BY lr.appliedDate DESC")
    List<LeaveRequest> findRecentByDepartment(@Param("deptId") Long deptId, Pageable pageable);
    
    // Check for overlapping leaves
    @Query("SELECT COUNT(lr) > 0 FROM LeaveRequest lr WHERE " +
           "lr.student.id = :studentId AND " +
           "lr.status NOT IN ('REJECTED_BY_HOD', 'REJECTED_BY_CLASS_ADVISOR', 'REJECTED_BY_MENTOR', 'CANCELLED') AND " +
           "((lr.startDate BETWEEN :startDate AND :endDate) OR " +
           "(lr.endDate BETWEEN :startDate AND :endDate) OR " +
           "(:startDate BETWEEN lr.startDate AND lr.endDate))")
    boolean hasOverlappingLeave(@Param("studentId") Long studentId, 
                                @Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);
}