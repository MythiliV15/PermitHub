package com.permithub.service.hod;

import com.permithub.entity.*;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import com.permithub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestManagementServiceImpl implements RequestManagementService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final ODRequestRepository odRequestRepository;
    private final OutpassRequestRepository outpassRequestRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    @Override
    public Page<LeaveRequest> getPendingLeaveRequests(Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        return leaveRequestRepository.findByDepartmentIdAndStatus(deptId, "PENDING", pageable);
    }

    @Override
    public Page<ODRequest> getPendingODRequests(Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        return odRequestRepository.findByDepartmentIdAndStatus(deptId, "PENDING", pageable);
    }

    @Override
    public Page<OutpassRequest> getPendingOutpassRequests(Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        return outpassRequestRepository.findByDepartmentIdAndStatus(deptId, "PENDING", pageable);
    }

    @Override
    public void approveLeaveRequest(Long id, String remarks) {
        LeaveRequest req = leaveRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        req.setStatus("APPROVED_BY_HOD");
        req.setApprovedAt(LocalDateTime.now());
        leaveRequestRepository.save(req);
        saveHistory(id, "LEAVE", "APPROVED", remarks);
    }

    @Override
    public void rejectLeaveRequest(Long id, String remarks) {
        LeaveRequest req = leaveRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        req.setStatus("REJECTED_BY_HOD");
        leaveRequestRepository.save(req);
        saveHistory(id, "LEAVE", "REJECTED", remarks);
    }

    @Override
    public void approveODRequest(Long id, String remarks) {
        ODRequest req = odRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
        req.setStatus("APPROVED_BY_HOD");
        req.setApprovedAt(LocalDateTime.now());
        odRequestRepository.save(req);
        saveHistory(id, "OD", "APPROVED", remarks);
    }

    @Override
    public void rejectODRequest(Long id, String remarks) {
        ODRequest req = odRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
        req.setStatus("REJECTED_BY_HOD");
        odRequestRepository.save(req);
        saveHistory(id, "OD", "REJECTED", remarks);
    }

    @Override
    public void approveOutpassRequest(Long id, String remarks) {
        OutpassRequest req = outpassRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
        req.setStatus("APPROVED_BY_HOD");
        req.setApprovedAt(LocalDateTime.now());
        outpassRequestRepository.save(req);
        saveHistory(id, "OUTPASS", "APPROVED", remarks);
    }

    @Override
    public void rejectOutpassRequest(Long id, String remarks) {
        OutpassRequest req = outpassRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
        req.setStatus("REJECTED_BY_HOD");
        outpassRequestRepository.save(req);
        saveHistory(id, "OUTPASS", "REJECTED", remarks);
    }

    private void saveHistory(Long requestId, String type, String status, String remarks) {
        ApprovalHistory history = ApprovalHistory.builder()
                .requestId(requestId)
                .requestType(type)
                .approverId(SecurityUtils.getCurrentUserId())
                .approverRole("HOD")
                .status(status)
                .remarks(remarks)
                .build();
        approvalHistoryRepository.save(history);
    }
}
