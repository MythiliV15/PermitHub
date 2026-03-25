package com.permithub.service.hod;

import com.permithub.entity.LeaveRequest;
import com.permithub.entity.ODRequest;
import com.permithub.entity.OutpassRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestManagementService {
    
    Page<LeaveRequest> getPendingLeaveRequests(Pageable pageable);
    Page<ODRequest> getPendingODRequests(Pageable pageable);
    Page<OutpassRequest> getPendingOutpassRequests(Pageable pageable);
    
    void approveLeaveRequest(Long id, String remarks);
    void rejectLeaveRequest(Long id, String remarks);
    
    void approveODRequest(Long id, String remarks);
    void rejectODRequest(Long id, String remarks);
    
    void approveOutpassRequest(Long id, String remarks);
    void rejectOutpassRequest(Long id, String remarks);
}
