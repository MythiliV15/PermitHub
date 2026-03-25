package com.permithub.service.hod;

import com.permithub.dto.hod.ApprovalRequestDTO;
import com.permithub.dto.hod.ApprovalResponseDTO;
import com.permithub.entity.*;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import com.permithub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HODApprovalServiceImpl implements HODApprovalService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final ODRequestRepository odRequestRepository;
    private final OutpassRequestRepository outpassRequestRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    public Page<Map<String, Object>> getPendingApprovals(Long hodId, Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<Map<String, Object>> allPending = new ArrayList<>();
        
        leaveRequestRepository.findPendingHODApprovals(deptId).forEach(lr -> allPending.add(mapLeaveToMap(lr)));
        odRequestRepository.findPendingHODApprovals(deptId).forEach(or -> allPending.add(mapODToMap(or)));
        outpassRequestRepository.findPendingHODApprovals(deptId).forEach(op -> allPending.add(mapOutpassToMap(op)));
        
        allPending.sort((a,b) -> {
            LocalDateTime dateA = (LocalDateTime) a.get("appliedDate");
            LocalDateTime dateB = (LocalDateTime) b.get("appliedDate");
            return dateB != null && dateA != null ? dateB.compareTo(dateA) : 0;
        });
        
        int start = (int) pageable.getOffset();
        if (start >= allPending.size()) return new PageImpl<>(new ArrayList<>(), pageable, allPending.size());
        int end = Math.min((start + pageable.getPageSize()), allPending.size());
        return new PageImpl<>(allPending.subList(start, end), pageable, allPending.size());
    }

    @Override
    public Page<Map<String, Object>> getPendingLeaveApprovals(Long hodId, Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<Map<String, Object>> mapped = leaveRequestRepository.findPendingHODApprovals(deptId).stream()
                .map(this::mapLeaveToMap).collect(Collectors.toList());
        return new PageImpl<>(mapped, pageable, mapped.size());
    }

    @Override
    public Page<Map<String, Object>> getPendingODApprovals(Long hodId, Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<Map<String, Object>> mapped = odRequestRepository.findPendingHODApprovals(deptId).stream()
                .map(this::mapODToMap).collect(Collectors.toList());
        return new PageImpl<>(mapped, pageable, mapped.size());
    }

    @Override
    public Page<Map<String, Object>> getPendingOutpassApprovals(Long hodId, Pageable pageable) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        List<Map<String, Object>> mapped = outpassRequestRepository.findPendingHODApprovals(deptId).stream()
                .map(this::mapOutpassToMap).collect(Collectors.toList());
        return new PageImpl<>(mapped, pageable, mapped.size());
    }

    @Override
    public ApprovalResponseDTO approveRequest(Long hodId, ApprovalRequestDTO dto) {
        log.info("HOD ID: {} approving request: {} of type {}", hodId, dto.getRequestId(), dto.getRequestType());
        String status = RequestStatus.APPROVED_BY_HOD.name();
        
        if ("LEAVE".equalsIgnoreCase(dto.getRequestType())) {
            LeaveRequest req = leaveRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
            req.setStatus(status);
            req.setHodId(hodId);
            req.setApprovedAt(LocalDateTime.now());
            leaveRequestRepository.save(req);
        } else if ("OD".equalsIgnoreCase(dto.getRequestType())) {
            ODRequest req = odRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
            req.setStatus(status);
            req.setHodId(hodId);
            req.setApprovedAt(LocalDateTime.now());
            odRequestRepository.save(req);
        } else if ("OUTPASS".equalsIgnoreCase(dto.getRequestType())) {
            OutpassRequest req = outpassRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
            req.setStatus(status);
            req.setHodId(hodId);
            req.setApprovedAt(LocalDateTime.now());
            outpassRequestRepository.save(req);
        }
        
        return ApprovalResponseDTO.builder().success(true).message("Approved successfully").requestId(dto.getRequestId()).build();
    }

    @Override
    public ApprovalResponseDTO rejectRequest(Long hodId, ApprovalRequestDTO dto) {
        log.info("HOD ID: {} rejecting request: {} of type {}", hodId, dto.getRequestId(), dto.getRequestType());
        String status = RequestStatus.REJECTED_BY_HOD.name();
        if ("LEAVE".equalsIgnoreCase(dto.getRequestType())) {
            LeaveRequest req = leaveRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
            req.setStatus(status);
            leaveRequestRepository.save(req);
        } else if ("OD".equalsIgnoreCase(dto.getRequestType())) {
            ODRequest req = odRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
            req.setStatus(status);
            odRequestRepository.save(req);
        } else if ("OUTPASS".equalsIgnoreCase(dto.getRequestType())) {
            OutpassRequest req = outpassRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
            req.setStatus(status);
            outpassRequestRepository.save(req);
        }
        return ApprovalResponseDTO.builder().success(true).message("Rejected successfully").requestId(dto.getRequestId()).build();
    }

    @Override
    public Page<Map<String, Object>> getApprovalHistory(Long hodId, String requestType, Pageable pageable) {
        List<Map<String, Object>> history = new ArrayList<>();
        leaveRequestRepository.findByHodId(hodId).forEach(lr -> history.add(mapLeaveToMap(lr)));
        odRequestRepository.findByHodId(hodId).forEach(or -> history.add(mapODToMap(or)));
        outpassRequestRepository.findByHodId(hodId).forEach(op -> history.add(mapOutpassToMap(op)));

        int start = (int) pageable.getOffset();
        if (start >= history.size()) return new PageImpl<>(new ArrayList<>(), pageable, history.size());
        int end = Math.min((start + pageable.getPageSize()), history.size());
        return new PageImpl<>(history.subList(start, end), pageable, history.size());
    }

    @Override
    public Map<String, Object> getRequestDetails(Long hodId, String type, Long requestId) {
        if ("LEAVE".equalsIgnoreCase(type)) {
            LeaveRequest lr = leaveRequestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
            return mapLeaveToMap(lr);
        } else if ("OD".equalsIgnoreCase(type)) {
            ODRequest or = odRequestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("OD not found"));
            return mapODToMap(or);
        } else if ("OUTPASS".equalsIgnoreCase(type)) {
            OutpassRequest op = outpassRequestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Outpass not found"));
            return mapOutpassToMap(op);
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getApprovalStatistics(Long hodId) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingLeaves", (long) leaveRequestRepository.findPendingHODApprovals(deptId).size());
        stats.put("pendingODs", (long) odRequestRepository.findPendingHODApprovals(deptId).size());
        stats.put("pendingOutpass", (long) outpassRequestRepository.findPendingHODApprovals(deptId).size());
        return stats;
    }

    @Override
    public Map<String, Object> bulkApproveRequests(Long hodId, String type, List<Long> requestIds, String remarks) {
        for (Long id : requestIds) {
            approveRequest(hodId, new ApprovalRequestDTO(id, type, "APPROVE", remarks, true));
        }
        Map<String, Object> res = new HashMap<>();
        res.put("successful", requestIds.size());
        res.put("total", requestIds.size());
        return res;
    }

    @Override
    public Map<String, Object> bulkRejectRequests(Long hodId, String type, List<Long> requestIds, String remarks) {
        for (Long id : requestIds) {
            rejectRequest(hodId, new ApprovalRequestDTO(id, type, "REJECT", remarks, true));
        }
        Map<String, Object> res = new HashMap<>();
        res.put("successful", requestIds.size());
        res.put("total", requestIds.size());
        return res;
    }

    private Map<String, Object> mapLeaveToMap(LeaveRequest lr) {
        StudentProfile student = studentProfileRepository.findById(lr.getStudentId()).orElse(null);
        Map<String, Object> map = new HashMap<>();
        map.put("id", lr.getId());
        map.put("type", "LEAVE");
        map.put("studentName", student != null ? student.getName() : "Unknown");
        map.put("registerNumber", student != null ? student.getRegNo() : "N/A");
        map.put("appliedDate", lr.getAppliedAt());
        map.put("startDate", lr.getStartDate());
        map.put("endDate", lr.getEndDate());
        map.put("reason", lr.getReason());
        map.put("status", lr.getStatus());
        return map;
    }

    private Map<String, Object> mapODToMap(ODRequest or) {
        StudentProfile student = studentProfileRepository.findById(or.getStudentId()).orElse(null);
        Map<String, Object> map = new HashMap<>();
        map.put("id", or.getId());
        map.put("type", "OD");
        map.put("studentName", student != null ? student.getName() : "Unknown");
        map.put("registerNumber", student != null ? student.getRegNo() : "N/A");
        map.put("appliedDate", or.getAppliedAt());
        map.put("eventName", or.getEventName());
        map.put("status", or.getStatus());
        return map;
    }

    private Map<String, Object> mapOutpassToMap(OutpassRequest op) {
        StudentProfile student = studentProfileRepository.findById(op.getStudentId()).orElse(null);
        Map<String, Object> map = new HashMap<>();
        map.put("id", op.getId());
        map.put("type", "OUTPASS");
        map.put("studentName", student != null ? student.getName() : "Unknown");
        map.put("registerNumber", student != null ? student.getRegNo() : "N/A");
        map.put("appliedDate", op.getAppliedAt());
        map.put("reason", op.getReason());
        map.put("status", op.getStatus());
        return map;
    }
}
