package com.permithub.service.hod;

import com.permithub.dto.hod.ApprovalRequestDTO;
import com.permithub.dto.hod.ApprovalResponseDTO;
import com.permithub.entity.*;
import com.permithub.exception.BadRequestException;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
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

    private final FacultyRepository facultyRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ODRequestRepository odRequestRepository;
    private final OutpassRequestRepository outpassRequestRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Map<String, Object>> getPendingApprovals(Long hodId, Pageable pageable) {
        log.info("Fetching pending approvals for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Map<String, Object>> allPending = new ArrayList<>();
        
        // Get pending leave requests
        List<LeaveRequest> pendingLeaves = leaveRequestRepository.findPendingHODApprovals(deptId);
        allPending.addAll(pendingLeaves.stream()
                .map(this::mapLeaveRequestToMap)
                .collect(Collectors.toList()));
        
        // Get pending OD requests
        List<ODRequest> pendingODs = odRequestRepository.findPendingHODApprovals(deptId);
        allPending.addAll(pendingODs.stream()
                .map(this::mapODRequestToMap)
                .collect(Collectors.toList()));
        
        // Get pending outpass requests
        List<OutpassRequest> pendingOutpass = outpassRequestRepository.findPendingHODApprovals(deptId);
        allPending.addAll(pendingOutpass.stream()
                .map(this::mapOutpassRequestToMap)
                .collect(Collectors.toList()));
        
        // Sort by applied date (newest first)
        allPending.sort((a, b) -> {
            LocalDateTime dateA = (LocalDateTime) a.get("appliedDate");
            LocalDateTime dateB = (LocalDateTime) b.get("appliedDate");
            return dateB.compareTo(dateA);
        });
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPending.size());
        
        return new PageImpl<>(allPending.subList(start, end), pageable, allPending.size());
    }

    @Override
    public Page<Map<String, Object>> getPendingLeaveApprovals(Long hodId, Pageable pageable) {
        log.info("Fetching pending leave approvals for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<LeaveRequest> pendingLeaves = leaveRequestRepository.findPendingHODApprovals(deptId);
        
        List<Map<String, Object>> mappedLeaves = pendingLeaves.stream()
                .map(this::mapLeaveRequestToMap)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), mappedLeaves.size());
        
        return new PageImpl<>(mappedLeaves.subList(start, end), pageable, mappedLeaves.size());
    }

    @Override
    public Page<Map<String, Object>> getPendingODApprovals(Long hodId, Pageable pageable) {
        log.info("Fetching pending OD approvals for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<ODRequest> pendingODs = odRequestRepository.findPendingHODApprovals(deptId);
        
        List<Map<String, Object>> mappedODs = pendingODs.stream()
                .map(this::mapODRequestToMap)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), mappedODs.size());
        
        return new PageImpl<>(mappedODs.subList(start, end), pageable, mappedODs.size());
    }

    @Override
    public Page<Map<String, Object>> getPendingOutpassApprovals(Long hodId, Pageable pageable) {
        log.info("Fetching pending outpass approvals for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<OutpassRequest> pendingOutpass = outpassRequestRepository.findPendingHODApprovals(deptId);
        
        List<Map<String, Object>> mappedOutpass = pendingOutpass.stream()
                .map(this::mapOutpassRequestToMap)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), mappedOutpass.size());
        
        return new PageImpl<>(mappedOutpass.subList(start, end), pageable, mappedOutpass.size());
    }

    @Override
    public ApprovalResponseDTO approveRequest(Long hodId, ApprovalRequestDTO approvalDTO) {
        log.info("HOD ID: {} approving request - Type: {}, ID: {}", hodId, approvalDTO.getRequestType(), approvalDTO.getRequestId());
        
        Faculty hod = getHodFaculty(hodId);
        
        switch (approvalDTO.getRequestType().toUpperCase()) {
            case "LEAVE":
                return approveLeaveRequest(hod, approvalDTO);
            case "OD":
                return approveODRequest(hod, approvalDTO);
            case "OUTPASS":
                return approveOutpassRequest(hod, approvalDTO);
            default:
                throw new BadRequestException("Invalid request type: " + approvalDTO.getRequestType());
        }
    }

    @Override
    public ApprovalResponseDTO rejectRequest(Long hodId, ApprovalRequestDTO approvalDTO) {
        log.info("HOD ID: {} rejecting request - Type: {}, ID: {}", hodId, approvalDTO.getRequestType(), approvalDTO.getRequestId());
        
        Faculty hod = getHodFaculty(hodId);
        
        switch (approvalDTO.getRequestType().toUpperCase()) {
            case "LEAVE":
                return rejectLeaveRequest(hod, approvalDTO);
            case "OD":
                return rejectODRequest(hod, approvalDTO);
            case "OUTPASS":
                return rejectOutpassRequest(hod, approvalDTO);
            default:
                throw new BadRequestException("Invalid request type: " + approvalDTO.getRequestType());
        }
    }

    @Override
    public Page<Map<String, Object>> getApprovalHistory(Long hodId, String requestType, Pageable pageable) {
        log.info("Fetching approval history for HOD ID: {}, type: {}", hodId, requestType);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Map<String, Object>> history = new ArrayList<>();
        
        if (requestType == null || requestType.equalsIgnoreCase("LEAVE")) {
            // Get leave requests that were approved/rejected by HOD
            List<LeaveRequest> leaveHistory = leaveRequestRepository.findByHod(hod).stream()
                    .filter(lr -> lr.getStatus() == RequestStatus.APPROVED_BY_HOD || 
                                   lr.getStatus() == RequestStatus.REJECTED_BY_HOD)
                    .collect(Collectors.toList());
            history.addAll(leaveHistory.stream()
                    .map(this::mapLeaveRequestToHistoryMap)
                    .collect(Collectors.toList()));
        }
        
        if (requestType == null || requestType.equalsIgnoreCase("OD")) {
            List<ODRequest> odHistory = odRequestRepository.findByHod(hod).stream()
                    .filter(od -> od.getStatus() == RequestStatus.APPROVED_BY_HOD || 
                                   od.getStatus() == RequestStatus.REJECTED_BY_HOD)
                    .collect(Collectors.toList());
            history.addAll(odHistory.stream()
                    .map(this::mapODRequestToHistoryMap)
                    .collect(Collectors.toList()));
        }
        
        if (requestType == null || requestType.equalsIgnoreCase("OUTPASS")) {
            List<OutpassRequest> outpassHistory = outpassRequestRepository.findByHod(hod).stream()
                    .filter(op -> op.getStatus() == RequestStatus.APPROVED_BY_HOD || 
                                   op.getStatus() == RequestStatus.REJECTED_BY_HOD)
                    .collect(Collectors.toList());
            history.addAll(outpassHistory.stream()
                    .map(this::mapOutpassRequestToHistoryMap)
                    .collect(Collectors.toList()));
        }
        
        // Sort by action date (newest first)
        history.sort((a, b) -> {
            LocalDateTime dateA = (LocalDateTime) a.get("actionDate");
            LocalDateTime dateB = (LocalDateTime) b.get("actionDate");
            return dateB.compareTo(dateA);
        });
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), history.size());
        
        return new PageImpl<>(history.subList(start, end), pageable, history.size());
    }

    @Override
    public Map<String, Object> getRequestDetails(Long hodId, String requestType, Long requestId) {
        log.info("Fetching request details - Type: {}, ID: {}", requestType, requestId);
        
        Faculty hod = getHodFaculty(hodId);
        
        switch (requestType.toUpperCase()) {
            case "LEAVE":
                LeaveRequest leave = leaveRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
                verifyDepartment(leave.getStudent().getDepartment(), hod);
                return mapLeaveRequestToDetailsMap(leave);
                
            case "OD":
                ODRequest od = odRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
                verifyDepartment(od.getStudent().getDepartment(), hod);
                return mapODRequestToDetailsMap(od);
                
            case "OUTPASS":
                OutpassRequest outpass = outpassRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
                verifyDepartment(outpass.getStudent().getDepartment(), hod);
                return mapOutpassRequestToDetailsMap(outpass);
                
            default:
                throw new BadRequestException("Invalid request type: " + requestType);
        }
    }

    @Override
    public Map<String, Object> getApprovalStatistics(Long hodId) {
        log.info("Fetching approval statistics for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Pending counts
        stats.put("pendingLeaves", leaveRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId));
        stats.put("pendingODs", odRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId));
        stats.put("pendingOutpass", outpassRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_WARDEN, deptId));
        
        // Approved counts (this month)
        stats.put("approvedThisMonth", getApprovedCountThisMonth(deptId));
        
        // Rejected counts (this month)
        stats.put("rejectedThisMonth", getRejectedCountThisMonth(deptId));
        
        // Average processing time
        stats.put("avgProcessingTime", getAverageProcessingTime(deptId));
        
        // Approval rate
        stats.put("approvalRate", getApprovalRate(deptId));
        
        return stats;
    }

    @Override
    public Map<String, Object> bulkApproveRequests(Long hodId, String requestType, List<Long> requestIds, String remarks) {
        log.info("Bulk approving {} requests of type: {}", requestIds.size(), requestType);
        
        Map<String, Object> result = new HashMap<>();
        List<Long> successful = new ArrayList<>();
        List<Map<String, Object>> failed = new ArrayList<>();
        
        for (Long requestId : requestIds) {
            try {
                ApprovalRequestDTO dto = ApprovalRequestDTO.builder()
                        .requestId(requestId)
                        .requestType(requestType)
                        .action("APPROVE")
                        .remarks(remarks)
                        .build();
                
                ApprovalResponseDTO response = approveRequest(hodId, dto);
                successful.add(requestId);
                
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("requestId", requestId);
                error.put("error", e.getMessage());
                failed.add(error);
                log.error("Failed to approve request {}: {}", requestId, e.getMessage());
            }
        }
        
        result.put("total", requestIds.size());
        result.put("successful", successful.size());
        result.put("failed", failed.size());
        result.put("successfulIds", successful);
        result.put("failedDetails", failed);
        result.put("message", String.format("Bulk approval completed: %d succeeded, %d failed", 
                successful.size(), failed.size()));
        
        return result;
    }

    @Override
    public Map<String, Object> bulkRejectRequests(Long hodId, String requestType, List<Long> requestIds, String remarks) {
        log.info("Bulk rejecting {} requests of type: {}", requestIds.size(), requestType);
        
        Map<String, Object> result = new HashMap<>();
        List<Long> successful = new ArrayList<>();
        List<Map<String, Object>> failed = new ArrayList<>();
        
        for (Long requestId : requestIds) {
            try {
                ApprovalRequestDTO dto = ApprovalRequestDTO.builder()
                        .requestId(requestId)
                        .requestType(requestType)
                        .action("REJECT")
                        .remarks(remarks)
                        .build();
                
                ApprovalResponseDTO response = rejectRequest(hodId, dto);
                successful.add(requestId);
                
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("requestId", requestId);
                error.put("error", e.getMessage());
                failed.add(error);
                log.error("Failed to reject request {}: {}", requestId, e.getMessage());
            }
        }
        
        result.put("total", requestIds.size());
        result.put("successful", successful.size());
        result.put("failed", failed.size());
        result.put("successfulIds", successful);
        result.put("failedDetails", failed);
        result.put("message", String.format("Bulk rejection completed: %d succeeded, %d failed", 
                successful.size(), failed.size()));
        
        return result;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Faculty getHodFaculty(Long hodId) {
        return facultyRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found with ID: " + hodId));
    }

    private void verifyDepartment(Department requestDept, Faculty hod) {
        if (!requestDept.getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Request does not belong to your department");
        }
    }

    // ==================== LEAVE REQUEST METHODS ====================

    private ApprovalResponseDTO approveLeaveRequest(Faculty hod, ApprovalRequestDTO dto) {
        LeaveRequest request = leaveRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        
        verifyDepartment(request.getStudent().getDepartment(), hod);
        
        if (request.getStatus() != RequestStatus.APPROVED_BY_CLASS_ADVISOR) {
            throw new BadRequestException("Leave request is not ready for HOD approval. Current status: " + request.getStatus());
        }
        
        request.setStatus(RequestStatus.APPROVED_BY_HOD);
        request.setHod(hod);
        request.setHodRemark(dto.getRemarks());
        request.setHodActionDate(LocalDateTime.now());
        
        leaveRequestRepository.save(request);
        
        log.info("Leave request {} approved by HOD", request.getId());
        
        return ApprovalResponseDTO.builder()
                .requestId(request.getId())
                .requestType("LEAVE")
                .studentName(request.getStudent().getFullName())
                .registerNumber(request.getStudent().getRegisterNumber())
                .currentStatus(RequestStatus.APPROVED_BY_HOD)
                .action("APPROVED")
                .remarks(dto.getRemarks())
                .approvedBy(hod.getFullName())
                .approverRole("HOD")
                .actionDate(LocalDateTime.now())
                .success(true)
                .message("Leave request approved successfully")
                .build();
    }

    private ApprovalResponseDTO rejectLeaveRequest(Faculty hod, ApprovalRequestDTO dto) {
        LeaveRequest request = leaveRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        
        verifyDepartment(request.getStudent().getDepartment(), hod);
        
        request.setStatus(RequestStatus.REJECTED_BY_HOD);
        request.setHod(hod);
        request.setHodRemark(dto.getRemarks());
        request.setHodActionDate(LocalDateTime.now());
        
        leaveRequestRepository.save(request);
        
        log.info("Leave request {} rejected by HOD", request.getId());
        
        return ApprovalResponseDTO.builder()
                .requestId(request.getId())
                .requestType("LEAVE")
                .studentName(request.getStudent().getFullName())
                .registerNumber(request.getStudent().getRegisterNumber())
                .currentStatus(RequestStatus.REJECTED_BY_HOD)
                .action("REJECTED")
                .remarks(dto.getRemarks())
                .approvedBy(hod.getFullName())
                .approverRole("HOD")
                .actionDate(LocalDateTime.now())
                .success(true)
                .message("Leave request rejected")
                .build();
    }

    // ==================== OD REQUEST METHODS ====================

    private ApprovalResponseDTO approveODRequest(Faculty hod, ApprovalRequestDTO dto) {
        ODRequest request = odRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
        
        verifyDepartment(request.getStudent().getDepartment(), hod);
        
        if (request.getStatus() != RequestStatus.APPROVED_BY_CLASS_ADVISOR) {
            throw new BadRequestException("OD request is not ready for HOD approval. Current status: " + request.getStatus());
        }
        
        request.setStatus(RequestStatus.APPROVED_BY_HOD);
        request.setHod(hod);
        request.setHodRemark(dto.getRemarks());
        request.setHodActionDate(LocalDateTime.now());
        
        odRequestRepository.save(request);
        
        log.info("OD request {} approved by HOD", request.getId());
        
        return ApprovalResponseDTO.builder()
                .requestId(request.getId())
                .requestType("OD")
                .studentName(request.getStudent().getFullName())
                .registerNumber(request.getStudent().getRegisterNumber())
                .currentStatus(RequestStatus.APPROVED_BY_HOD)
                .action("APPROVED")
                .remarks(dto.getRemarks())
                .approvedBy(hod.getFullName())
                .approverRole("HOD")
                .actionDate(LocalDateTime.now())
                .success(true)
                .message("OD request approved successfully")
                .build();
    }

    private ApprovalResponseDTO rejectODRequest(Faculty hod, ApprovalRequestDTO dto) {
        ODRequest request = odRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("OD request not found"));
        
        verifyDepartment(request.getStudent().getDepartment(), hod);
        
        request.setStatus(RequestStatus.REJECTED_BY_HOD);
        request.setHod(hod);
        request.setHodRemark(dto.getRemarks());
        request.setHodActionDate(LocalDateTime.now());
        
        odRequestRepository.save(request);
        
        log.info("OD request {} rejected by HOD", request.getId());
        
        return ApprovalResponseDTO.builder()
                .requestId(request.getId())
                .requestType("OD")
                .studentName(request.getStudent().getFullName())
                .registerNumber(request.getStudent().getRegisterNumber())
                .currentStatus(RequestStatus.REJECTED_BY_HOD)
                .action("REJECTED")
                .remarks(dto.getRemarks())
                .approvedBy(hod.getFullName())
                .approverRole("HOD")
                .actionDate(LocalDateTime.now())
                .success(true)
                .message("OD request rejected")
                .build();
    }

    // ==================== OUTPASS REQUEST METHODS ====================

    private ApprovalResponseDTO approveOutpassRequest(Faculty hod, ApprovalRequestDTO dto) {
        OutpassRequest request = outpassRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
        
        verifyDepartment(request.getStudent().getDepartment(), hod);
        
        if (request.getStatus() != RequestStatus.APPROVED_BY_WARDEN) {
            throw new BadRequestException("Outpass request is not ready for HOD approval. Current status: " + request.getStatus());
        }
        
        request.setStatus(RequestStatus.APPROVED_BY_HOD);
        request.setHodRemark(dto.getRemarks());
        request.setHodActionDate(LocalDateTime.now());
        
        outpassRequestRepository.save(request);
        
        log.info("Outpass request {} approved by HOD", request.getId());
        
        return ApprovalResponseDTO.builder()
                .requestId(request.getId())
                .requestType("OUTPASS")
                .studentName(request.getStudent().getFullName())
                .registerNumber(request.getStudent().getRegisterNumber())
                .currentStatus(RequestStatus.APPROVED_BY_HOD)
                .action("APPROVED")
                .remarks(dto.getRemarks())
                .approvedBy(hod.getFullName())
                .approverRole("HOD")
                .actionDate(LocalDateTime.now())
                .success(true)
                .message("Outpass request approved successfully")
                .build();
    }

    private ApprovalResponseDTO rejectOutpassRequest(Faculty hod, ApprovalRequestDTO dto) {
        OutpassRequest request = outpassRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Outpass request not found"));
        
        verifyDepartment(request.getStudent().getDepartment(), hod);
        
        request.setStatus(RequestStatus.REJECTED_BY_HOD);
        request.setHodRemark(dto.getRemarks());
        request.setHodActionDate(LocalDateTime.now());
        
        outpassRequestRepository.save(request);
        
        log.info("Outpass request {} rejected by HOD", request.getId());
        
        return ApprovalResponseDTO.builder()
                .requestId(request.getId())
                .requestType("OUTPASS")
                .studentName(request.getStudent().getFullName())
                .registerNumber(request.getStudent().getRegisterNumber())
                .currentStatus(RequestStatus.REJECTED_BY_HOD)
                .action("REJECTED")
                .remarks(dto.getRemarks())
                .approvedBy(hod.getFullName())
                .approverRole("HOD")
                .actionDate(LocalDateTime.now())
                .success(true)
                .message("Outpass request rejected")
                .build();
    }

    // ==================== MAPPING METHODS ====================

    private Map<String, Object> mapLeaveRequestToMap(LeaveRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        map.put("type", "LEAVE");
        map.put("studentId", request.getStudent().getId());
        map.put("studentName", request.getStudent().getFullName());
        map.put("registerNumber", request.getStudent().getRegisterNumber());
        map.put("year", request.getStudent().getYear());
        map.put("section", request.getStudent().getSection());
        map.put("startDate", request.getStartDate());
        map.put("endDate", request.getEndDate());
        map.put("totalDays", request.getTotalDays());
        map.put("category", request.getCategory());
        map.put("reason", request.getReason());
        map.put("status", request.getStatus());
        map.put("appliedDate", request.getAppliedDate());
        map.put("mentorRemark", request.getMentorRemark());
        map.put("classAdvisorRemark", request.getClassAdvisorRemark());
        return map;
    }

    private Map<String, Object> mapODRequestToMap(ODRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        map.put("type", "OD");
        map.put("studentId", request.getStudent().getId());
        map.put("studentName", request.getStudent().getFullName());
        map.put("registerNumber", request.getStudent().getRegisterNumber());
        map.put("year", request.getStudent().getYear());
        map.put("section", request.getStudent().getSection());
        map.put("startDate", request.getStartDate());
        map.put("endDate", request.getEndDate());
        map.put("totalDays", request.getTotalDays());
        map.put("eventType", request.getEventType());
        map.put("eventName", request.getEventName());
        map.put("location", request.getLocation());
        map.put("status", request.getStatus());
        map.put("appliedDate", request.getAppliedDate());
        map.put("mentorRemark", request.getMentorRemark());
        map.put("coordinatorRemark", request.getCoordinatorRemark());
        map.put("classAdvisorRemark", request.getClassAdvisorRemark());
        return map;
    }

    private Map<String, Object> mapOutpassRequestToMap(OutpassRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        map.put("type", "OUTPASS");
        map.put("studentId", request.getStudent().getId());
        map.put("studentName", request.getStudent().getFullName());
        map.put("registerNumber", request.getStudent().getRegisterNumber());
        map.put("year", request.getStudent().getYear());
        map.put("section", request.getStudent().getSection());
        map.put("outDateTime", request.getOutDateTime());
        map.put("expectedReturnDateTime", request.getExpectedReturnDateTime());
        map.put("destination", request.getDestination());
        map.put("reason", request.getReason());
        map.put("status", request.getStatus());
        map.put("appliedDate", request.getAppliedDate());
        map.put("parentStatus", request.getParentActionDate() != null ? "APPROVED" : "PENDING");
        map.put("mentorRemark", request.getMentorRemark());
        map.put("classAdvisorRemark", request.getClassAdvisorRemark());
        map.put("wardenRemark", request.getWardenRemark());
        return map;
    }

    private Map<String, Object> mapLeaveRequestToHistoryMap(LeaveRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        map.put("type", "LEAVE");
        map.put("studentName", request.getStudent().getFullName());
        map.put("registerNumber", request.getStudent().getRegisterNumber());
        map.put("action", request.getStatus() == RequestStatus.APPROVED_BY_HOD ? "APPROVED" : "REJECTED");
        map.put("remarks", request.getHodRemark());
        map.put("actionDate", request.getHodActionDate());
        map.put("details", request.getStartDate() + " to " + request.getEndDate() + " (" + request.getTotalDays() + " days)");
        return map;
    }

    private Map<String, Object> mapODRequestToHistoryMap(ODRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        map.put("type", "OD");
        map.put("studentName", request.getStudent().getFullName());
        map.put("registerNumber", request.getStudent().getRegisterNumber());
        map.put("action", request.getStatus() == RequestStatus.APPROVED_BY_HOD ? "APPROVED" : "REJECTED");
        map.put("remarks", request.getHodRemark());
        map.put("actionDate", request.getHodActionDate());
        map.put("details", request.getEventName() + " (" + request.getEventType() + ")");
        return map;
    }

    private Map<String, Object> mapOutpassRequestToHistoryMap(OutpassRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        map.put("type", "OUTPASS");
        map.put("studentName", request.getStudent().getFullName());
        map.put("registerNumber", request.getStudent().getRegisterNumber());
        map.put("action", request.getStatus() == RequestStatus.APPROVED_BY_HOD ? "APPROVED" : "REJECTED");
        map.put("remarks", request.getHodRemark());
        map.put("actionDate", request.getHodActionDate());
        map.put("details", request.getDestination() + " on " + request.getOutDateTime().toLocalDate());
        return map;
    }

    private Map<String, Object> mapLeaveRequestToDetailsMap(LeaveRequest request) {
        Map<String, Object> map = mapLeaveRequestToMap(request);
        map.put("studentEmail", request.getStudent().getEmail());
        map.put("studentPhone", request.getStudent().getPhoneNumber());
        map.put("parentName", request.getStudent().getParentName());
        map.put("parentPhone", request.getStudent().getParentPhone());
        map.put("medicalCertificate", request.getMedicalCertificatePath());
        map.put("isEmergency", request.getIsEmergency());
        map.put("mentorName", request.getMentor() != null ? request.getMentor().getFullName() : null);
        map.put("classAdvisorName", request.getClassAdvisor() != null ? request.getClassAdvisor().getFullName() : null);
        return map;
    }

    private Map<String, Object> mapODRequestToDetailsMap(ODRequest request) {
        Map<String, Object> map = mapODRequestToMap(request);
        map.put("studentEmail", request.getStudent().getEmail());
        map.put("studentPhone", request.getStudent().getPhoneNumber());
        map.put("organizer", request.getOrganizer());
        map.put("description", request.getDescription());
        map.put("proofDocument", request.getProofDocumentPath());
        map.put("isOutstation", request.getIsOutstation());
        map.put("accommodationRequired", request.getAccommodationRequired());
        map.put("mentorName", request.getMentor() != null ? request.getMentor().getFullName() : null);
        map.put("coordinatorName", request.getEventCoordinator() != null ? request.getEventCoordinator().getFullName() : null);
        map.put("classAdvisorName", request.getClassAdvisor() != null ? request.getClassAdvisor().getFullName() : null);
        return map;
    }

    private Map<String, Object> mapOutpassRequestToDetailsMap(OutpassRequest request) {
        Map<String, Object> map = mapOutpassRequestToMap(request);
        map.put("studentEmail", request.getStudent().getEmail());
        map.put("studentPhone", request.getStudent().getPhoneNumber());
        map.put("emergencyContact", request.getEmergencyContact());
        map.put("actualReturnDateTime", request.getActualReturnDateTime());
        map.put("isLateEntry", request.getIsLateEntry());
        map.put("lateMinutes", request.getLateMinutes());
        map.put("parentApproved", request.getParentActionDate() != null);
        map.put("parentApprovedDate", request.getParentActionDate());
        map.put("parentRemark", request.getParentRemark());
        map.put("mentorName", request.getMentor() != null ? request.getMentor().getFullName() : null);
        map.put("classAdvisorName", request.getClassAdvisor() != null ? request.getClassAdvisor().getFullName() : null);
        map.put("wardenName", request.getWarden() != null ? request.getWarden().getFullName() : null);
        return map;
    }

    // ==================== STATISTICS METHODS ====================

    private long getApprovedCountThisMonth(Long deptId) {
        // This would require custom queries with date filtering
        // Placeholder implementation
        return 0;
    }

    private long getRejectedCountThisMonth(Long deptId) {
        // Placeholder implementation
        return 0;
    }

    private double getAverageProcessingTime(Long deptId) {
        // Placeholder implementation
        return 2.5; // days
    }

    private double getApprovalRate(Long deptId) {
        // Placeholder implementation
        return 85.5; // percentage
    }
}
