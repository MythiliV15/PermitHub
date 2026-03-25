package com.permithub.controller.hod;

import com.permithub.dto.response.ApiResponse;
import com.permithub.entity.LeaveRequest;
import com.permithub.entity.ODRequest;
import com.permithub.entity.OutpassRequest;
import com.permithub.service.hod.RequestManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hod/requests")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HOD')")
public class HODRequestController {

    private final RequestManagementService requestService;

    @GetMapping("/leave/pending")
    public ResponseEntity<ApiResponse<Page<LeaveRequest>>> getPendingLeaves(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Pending leaves fetched", requestService.getPendingLeaveRequests(pageable)));
    }

    @GetMapping("/od/pending")
    public ResponseEntity<ApiResponse<Page<ODRequest>>> getPendingODs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Pending ODs fetched", requestService.getPendingODRequests(pageable)));
    }

    @GetMapping("/outpass/pending")
    public ResponseEntity<ApiResponse<Page<OutpassRequest>>> getPendingOutpasses(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Pending outpasses fetched", requestService.getPendingOutpassRequests(pageable)));
    }

    @PostMapping("/leave/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveLeave(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        requestService.approveLeaveRequest(id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Leave approved"));
    }

    @PostMapping("/leave/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectLeave(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        requestService.rejectLeaveRequest(id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Leave rejected"));
    }

    @PostMapping("/od/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveOD(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        requestService.approveODRequest(id, remarks);
        return ResponseEntity.ok(ApiResponse.success("OD approved"));
    }

    @PostMapping("/od/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectOD(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        requestService.rejectODRequest(id, remarks);
        return ResponseEntity.ok(ApiResponse.success("OD rejected"));
    }

    @PostMapping("/outpass/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveOutpass(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        requestService.approveOutpassRequest(id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Outpass approved"));
    }

    @PostMapping("/outpass/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectOutpass(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        requestService.rejectOutpassRequest(id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Outpass rejected"));
    }
}
