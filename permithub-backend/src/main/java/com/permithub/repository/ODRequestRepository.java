package com.permithub.repository;

import com.permithub.entity.ODRequest;
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
import java.util.List;

@Repository
public interface ODRequestRepository extends JpaRepository<ODRequest, Long> {
    
    // Find by student
    List<ODRequest> findByStudent(Student student);
    
    Page<ODRequest> findByStudent(Student student, Pageable pageable);
    
    // Find by status
    List<ODRequest> findByStatus(RequestStatus status);
    
    // Find pending approvals for HOD (after class advisor approval)
    @Query("SELECT odr FROM ODRequest odr WHERE odr.status = 'APPROVED_BY_CLASS_ADVISOR' AND odr.student.department.id = :deptId")
    List<ODRequest> findPendingHODApprovals(@Param("deptId") Long deptId);
    
    // Find by event type
    List<ODRequest> findByEventType(String eventType);
    
    // Count by status for a department
    @Query("SELECT COUNT(odr) FROM ODRequest odr WHERE odr.status = :status AND odr.student.department.id = :deptId")
    long countByStatusAndDepartment(@Param("status") RequestStatus status, @Param("deptId") Long deptId);
    
    // Find by mentor
    List<ODRequest> findByMentor(Faculty mentor);
    
    // Find by event coordinator
    List<ODRequest> findByEventCoordinator(Faculty coordinator);
    
    // Find by class advisor
    List<ODRequest> findByClassAdvisor(Faculty classAdvisor);
    
    // Find by HOD
    List<ODRequest> findByHod(Faculty hod);
    
    // Search with filters
    @Query("SELECT odr FROM ODRequest odr WHERE " +
           "(:studentName IS NULL OR odr.student.fullName LIKE %:studentName%) AND " +
           "(:status IS NULL OR odr.status = :status) AND " +
           "(:eventType IS NULL OR odr.eventType = :eventType) AND " +
           "(:deptId IS NULL OR odr.student.department.id = :deptId)")
    Page<ODRequest> searchODRequests(
            @Param("studentName") String studentName,
            @Param("status") RequestStatus status,
            @Param("eventType") String eventType,
            @Param("deptId") Long deptId,
            Pageable pageable);
    
    // Get recent OD requests for a department
    @Query("SELECT odr FROM ODRequest odr WHERE odr.student.department.id = :deptId ORDER BY odr.appliedDate DESC")
    List<ODRequest> findRecentByDepartment(@Param("deptId") Long deptId, Pageable pageable);
}
