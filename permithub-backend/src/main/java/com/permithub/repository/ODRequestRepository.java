package com.permithub.repository;

import com.permithub.entity.ODRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ODRequestRepository extends JpaRepository<ODRequest, Long> {
    
    List<ODRequest> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    
    // For HOD Dashboard
    long countByDepartmentIdAndStatus(Long departmentId, String status);
    
    Page<ODRequest> findByDepartmentIdAndStatus(Long departmentId, String status, Pageable pageable);
    
    // For Advisors
    Page<ODRequest> findByAdvisorIdAndStatus(Long advisorId, String status, Pageable pageable);
    
    // Advanced queries for HOD (requests that passed Advisor stage)
    @Query("SELECT orq FROM ODRequest orq WHERE orq.departmentId = ?1 AND orq.status = 'APPROVED_BY_CLASS_ADVISOR'")
    List<ODRequest> findPendingHODApprovals(Long departmentId);
    
    List<ODRequest> findByHodId(Long hodId);
}
