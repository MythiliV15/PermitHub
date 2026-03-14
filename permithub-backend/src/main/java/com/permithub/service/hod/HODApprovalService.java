package com.permithub.service.hod;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.permithub.dto.hod.ApprovalRequestDTO;
import com.permithub.dto.hod.ApprovalResponseDTO;

public interface HODApprovalService {
    
    /**
     * Get all pending approvals for HOD
     * @param hodId The HOD user ID
     * @param pageable Pagination information
     * @return Page of pending approvals
     */
    Page<Map<String, Object>> getPendingApprovals(Long hodId, Pageable pageable);
    
    /**
     * Get pending leave requests
     * @param hodId The HOD user ID
     * @param pageable Pagination information
     * @return Page of pending leave requests
     */
    Page<Map<String, Object>> getPendingLeaveApprovals(Long hodId, Pageable pageable);
    
    /**
     * Get pending OD requests
     * @param hodId The HOD user ID
     * @param pageable Pagination information
     * @return Page of pending OD requests
     */
    Page<Map<String, Object>> getPendingODApprovals(Long hodId, Pageable pageable);
    
    /**
     * Get pending outpass requests
     * @param hodId The HOD user ID
     * @param pageable Pagination information
     * @return Page of pending outpass requests
     */
    Page<Map<String, Object>> getPendingOutpassApprovals(Long hodId, Pageable pageable);
    
    /**
     * Approve a request (leave/OD/outpass)
     * @param hodId The HOD user ID
     * @param approvalDTO Approval details
     * @return Approval response
     */
    ApprovalResponseDTO approveRequest(Long hodId, ApprovalRequestDTO approvalDTO);
    
    /**
     * Reject a request (leave/OD/outpass)
     * @param hodId The HOD user ID
     * @param approvalDTO Rejection details
     * @return Approval response
     */
    ApprovalResponseDTO rejectRequest(Long hodId, ApprovalRequestDTO approvalDTO);
    
    /**
     * Get approval history
     * @param hodId The HOD user ID
     * @param requestType Type of request (leave/OD/outpass)
     * @param pageable Pagination information
     * @return Page of approval history
     */
    Page<Map<String, Object>> getApprovalHistory(Long hodId, String requestType, Pageable pageable);
    
    /**
     * Get request details by ID
     * @param hodId The HOD user ID
     * @param requestType Type of request
     * @param requestId Request ID
     * @return Request details
     */
    Map<String, Object> getRequestDetails(Long hodId, String requestType, Long requestId);
    
    /**
     * Get approval statistics
     * @param hodId The HOD user ID
     * @return Approval statistics
     */
    Map<String, Object> getApprovalStatistics(Long hodId);
    
    /**
     * Bulk approve multiple requests
     * @param hodId The HOD user ID
     * @param requestType Type of request
     * @param requestIds List of request IDs to approve
     * @param remarks Common remarks
     * @return Bulk approval results
     */
    Map<String, Object> bulkApproveRequests(Long hodId, String requestType, List<Long> requestIds, String remarks);
    
    /**
     * Bulk reject multiple requests
     * @param hodId The HOD user ID
     * @param requestType Type of request
     * @param requestIds List of request IDs to reject
     * @param remarks Common remarks
     * @return Bulk rejection results
     */
    Map<String, Object> bulkRejectRequests(Long hodId, String requestType, List<Long> requestIds, String remarks);
}