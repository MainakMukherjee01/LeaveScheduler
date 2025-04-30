package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.dto.response.LeaveBalanceResponse;
import com.sap.fsad.leaveApp.dto.response.UserResponse;
import com.sap.fsad.leaveApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for user profile and management")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        UserResponse response = userService.convertToUserResponse(userService.getCurrentUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leave-balance")
    @Operation(summary = "Get current user leave balances")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveBalanceResponse>> getCurrentUserLeaveBalances() {
        List<LeaveBalanceResponse> balances = userService.getCurrentUserLeaveBalances();
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/{userId}/leave-balance")
    @Operation(summary = "Get leave balances for a specific user (ADMIN or MANAGER only)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveBalanceResponse>> getUserLeaveBalances(@PathVariable Long userId) {
        List<LeaveBalanceResponse> balances = userService.getUserLeaveBalances(userId);
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managed")
    @Operation(summary = "Get users managed by current user (MANAGER only)")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserResponse>> getManagedUsers() {
        List<UserResponse> users = userService.getUsersByManagerId(userService.getCurrentUser().getId());
        return ResponseEntity.ok(users);
    }
}