package com.permithub.repository;

import com.permithub.entity.OutpassRequest;
import com.permithub.entity.RequestStatus;
import com.permithub.entity.Student;
import com.permithub.entity.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OutpassRequestRepository extends JpaRepository<OutpassRequest, Long> {
    
    // Find by student
    List<OutpassRequest> findByStudent(Student student);
    
    Page<OutpassRequest> findByStudent(Student student, Pageable pageable);
    
    // Find by status
    List<OutpassRequest> findByStatus(RequestStatus status);
    
    // Find pending approvals for HOD (after warden approval)
    @Query("SELECT opr FROM OutpassRequest opr WHERE opr.status = 'APPROVED_BY_WARDEN' AND opr.student.department.id = :deptId")
    List<OutpassRequest> findPendingHODApprovals(@Param("deptId") Long deptId);
    
    // Find by parent token
    Optional<OutpassRequest> findByParentToken(String parentToken);
    
    // Find active outpasses (approved but not returned)
    @Query("SELECT opr FROM OutpassRequest opr WHERE opr.status = 'APPROVED_BY_PRINCIPAL' AND " +
           "opr.actualReturnDateTime IS NULL AND opr.expectedReturnDateTime > :now")
    List<OutpassRequest> findActiveOutpasses(@Param("now") LocalDateTime now);
    
    // Find late returns
    @Query("SELECT opr FROM OutpassRequest opr WHERE opr.status = 'APPROVED_BY_PRINCIPAL' AND " +
           "opr.actualReturnDateTime IS NULL AND opr.expectedReturnDateTime < :now")
    List<OutpassRequest> findLateReturns(@Param("now") LocalDateTime now);
    
    // Count by status for a department
    @Query("SELECT COUNT(opr) FROM OutpassRequest opr WHERE opr.status = :status AND opr.student.department.id = :deptId")
    long countByStatusAndDepartment(@Param("status") RequestStatus status, @Param("deptId") Long deptId);
    
    // Find by mentor
    List<OutpassRequest> findByMentor(Faculty mentor);
    
    // Find by class advisor
    List<OutpassRequest> findByClassAdvisor(Faculty classAdvisor);
    
    // Find by warden
    List<OutpassRequest> findByWarden(Faculty warden);
    
    // Find by HOD
    List<OutpassRequest> findByHod(Faculty hod);
    
    // Search with filters
    @Query("SELECT opr FROM OutpassRequest opr WHERE " +
           "(:studentName IS NULL OR opr.student.fullName LIKE %:studentName%) AND " +
           "(:status IS NULL OR opr.status = :status) AND " +
           "(:deptId IS NULL OR opr.student.department.id = :deptId) AND " +
           "(:fromDate IS NULL OR opr.outDateTime >= :fromDate) AND " +
           "(:toDate IS NULL OR opr.expectedReturnDateTime <= :toDate)")
    Page<OutpassRequest> searchOutpasses(
            @Param("studentName") String studentName,
            @Param("status") RequestStatus status,
            @Param("deptId") Long deptId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
