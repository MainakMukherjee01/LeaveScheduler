package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.dto.request.LeaveApprovalRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.LeaveResponse;
import com.sap.fsad.leaveApp.service.LeaveApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-approvals")
@Tag(name = "Leave Approval", description = "Endpoints for leave approval workflow")
public class LeaveApprovalController {

    @Autowired
    private LeaveApprovalService leaveApprovalService;

    @GetMapping("/pending")
    @Operation(summary = "Get pending leave applications for approval")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaveApplications() {
        List<LeaveResponse> pendingLeaves = leaveApprovalService.getPendingLeaveApplications();
        return ResponseEntity.ok(pendingLeaves);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a leave application")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> approveLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveApprovalRequest request) {
        ApiResponse response = leaveApprovalService.approveLeave(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a leave application")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> rejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveApprovalRequest request) {
        ApiResponse response = leaveApprovalService.rejectLeave(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    @Operation(summary = "Get approved leave applications")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveResponse>> getApprovedLeaveApplications() {
        List<LeaveResponse> approvedLeaves = leaveApprovalService.getApprovedLeaveApplications();
        return ResponseEntity.ok(approvedLeaves);
    }
}