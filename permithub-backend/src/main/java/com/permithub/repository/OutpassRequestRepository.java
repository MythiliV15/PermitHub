package com.permithub.repository;

import com.permithub.entity.OutpassRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutpassRequestRepository extends JpaRepository<OutpassRequest, Long> {
    
    List<OutpassRequest> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    
    // For HOD Dashboard
    long countByDepartmentIdAndStatus(Long departmentId, String status);
    
    Page<OutpassRequest> findByDepartmentIdAndStatus(Long departmentId, String status, Pageable pageable);
    
    // For Wardens
    Page<OutpassRequest> findByWardenIdAndStatus(Long wardenId, String status, Pageable pageable);
    
    // Advanced queries for HOD (requests that passed Warden stage or are direct)
    @Query("SELECT op FROM OutpassRequest op WHERE op.departmentId = ?1 AND op.status = 'APPROVED_BY_WARDEN'")
    List<OutpassRequest> findPendingHODApprovals(Long departmentId);
    
    List<OutpassRequest> findByHodId(Long hodId);
}
