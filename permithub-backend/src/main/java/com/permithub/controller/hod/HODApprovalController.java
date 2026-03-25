package com.permithub.controller.hod;

import com.permithub.dto.hod.ApprovalRequestDTO;
import com.permithub.dto.hod.ApprovalResponseDTO;
import com.permithub.dto.response.ApiResponse;
import com.permithub.security.CustomUserDetails;
import com.permithub.service.hod.HODApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hod/approvals")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HOD')")
public class HODApprovalController {

    private final HODApprovalService approvalService;

    @GetMapping({"/pending", "/requests/pending"})
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getPendingApprovals(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        Page<Map<String, Object>> pending = approvalService.getPendingApprovals(currentUser.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Pending approvals retrieved successfully", pending));
    }

    @GetMapping("/pending/leaves")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getPendingLeaves(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        Page<Map<String, Object>> leaves = approvalService.getPendingLeaveApprovals(currentUser.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Pending leave requests retrieved successfully", leaves));
    }

    @GetMapping("/pending/od")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getPendingOD(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        Page<Map<String, Object>> ods = approvalService.getPendingODApprovals(currentUser.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Pending OD requests retrieved successfully", ods));
    }

    @GetMapping("/pending/outpass")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getPendingOutpass(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        Page<Map<String, Object>> outpass = approvalService.getPendingOutpassApprovals(currentUser.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Pending outpass requests retrieved successfully", outpass));
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<ApprovalResponseDTO>> approveRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ApprovalRequestDTO approvalDTO) {
        
        ApprovalResponseDTO response = approvalService.approveRequest(currentUser.getId(), approvalDTO);
        return ResponseEntity.ok(ApiResponse.success("Request approved successfully", response));
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<ApprovalResponseDTO>> rejectRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ApprovalRequestDTO approvalDTO) {
        
        ApprovalResponseDTO response = approvalService.rejectRequest(currentUser.getId(), approvalDTO);
        return ResponseEntity.ok(ApiResponse.success("Request rejected successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getApprovalHistory(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("actionDate").descending());
        Page<Map<String, Object>> history = approvalService.getApprovalHistory(currentUser.getId(), type, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Approval history retrieved successfully", history));
    }

    @GetMapping("/request/{type}/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRequestDetails(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable String type,
            @PathVariable Long id) {
        
        Map<String, Object> details = approvalService.getRequestDetails(currentUser.getId(), type, id);
        return ResponseEntity.ok(ApiResponse.success("Request details retrieved successfully", details));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApprovalStatistics(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> stats = approvalService.getApprovalStatistics(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Approval statistics retrieved successfully", stats));
    }

    @PostMapping("/bulk-approve")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkApprove(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String type,
            @RequestBody List<Long> requestIds,
            @RequestParam(required = false) String remarks) {
        
        Map<String, Object> result = approvalService.bulkApproveRequests(
                currentUser.getId(), type, requestIds, remarks);
        
        return ResponseEntity.ok(ApiResponse.success("Bulk approval completed", result));
    }

    @PostMapping("/bulk-reject")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkReject(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String type,
            @RequestBody List<Long> requestIds,
            @RequestParam(required = false) String remarks) {
        
        Map<String, Object> result = approvalService.bulkRejectRequests(
                currentUser.getId(), type, requestIds, remarks);
        
        return ResponseEntity.ok(ApiResponse.success("Bulk rejection completed", result));
    }
}