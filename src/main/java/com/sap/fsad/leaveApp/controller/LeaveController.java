package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.dto.request.LeaveApplicationRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.LeaveBalanceResponse;
import com.sap.fsad.leaveApp.dto.response.CalendarEventResponse;
import com.sap.fsad.leaveApp.dto.response.LeaveResponse;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-applications")
@Tag(name = "Leave Management", description = "Endpoints for leave applications")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping
    @Operation(summary = "Apply for leave")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveApplicationRequest request) {
        LeaveResponse response = leaveService.applyLeave(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get current user's leave applications")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveResponse>> getCurrentUserLeaves() {
        List<LeaveResponse> leaves = leaveService.getCurrentUserLeaves();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/eligibility")
    @Operation(summary = "Get leave eligibility details for the current user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveEligibilityDetails() {
        List<LeaveBalanceResponse> eligibilityDetails = leaveService.getLeaveEligibilityDetails();
        return ResponseEntity.ok(eligibilityDetails);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get current user's pending leave applications")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveResponse>> getCurrentUserPendingLeaves() {
        List<LeaveResponse> leaves = leaveService.getCurrentUserPendingLeaves();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get leave application by ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        LeaveResponse response = leaveService.getLeaveById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/withdraw")
    @Operation(summary = "Withdraw a leave application")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> withdrawLeave(@PathVariable Long id) {
        ApiResponse response = leaveService.withdrawLeave(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get current user's leave history")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveResponse>> getLeaveHistory() {
        List<LeaveResponse> history = leaveService.getCurrentUserLeaves();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/filter-history")
    @Operation(summary = "Get leave history with optional filters")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveResponse>> getLeaveHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) LeaveType leaveType) {
        List<LeaveResponse> history = leaveService.getLeaveHistory(startDate, endDate, leaveType);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get current user's leave statistics")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LeaveResponse.LeaveStats> getLeaveStats() {
        LeaveResponse.LeaveStats stats = leaveService.getCurrentUserLeaveStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get leave schedules and holidays for calendar integration")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<CalendarEventResponse>> getCalendarEvents(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String department) {
        List<CalendarEventResponse> events = leaveService.getCalendarEvents(userId, department);
        return ResponseEntity.ok(events);
    }
}