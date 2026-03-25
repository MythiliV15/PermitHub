package com.permithub.repository;

import com.permithub.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    List<LeaveRequest> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    
    // For HOD Dashboard
    long countByDepartmentIdAndStatus(Long departmentId, String status);
    
    Page<LeaveRequest> findByDepartmentIdAndStatus(Long departmentId, String status, Pageable pageable);
    
    // For Mentors/Advisors
    Page<LeaveRequest> findByMentorIdAndStatus(Long mentorId, String status, Pageable pageable);
    Page<LeaveRequest> findByAdvisorIdAndStatus(Long advisorId, String status, Pageable pageable);
    
    // Advanced queries for HOD (requests that passed Advisor stage)
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.departmentId = ?1 AND lr.status = 'APPROVED_BY_CLASS_ADVISOR'")
    List<LeaveRequest> findPendingHODApprovals(Long departmentId);
    
    List<LeaveRequest> findByHodId(Long hodId);
}