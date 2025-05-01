package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.dto.request.LoginRequest;
import com.sap.fsad.leaveApp.dto.request.PasswordChangeRequest;
import com.sap.fsad.leaveApp.dto.request.RegisterRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.JwtResponse;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/register/batch")
    @Operation(summary = "Register multiple users")
    public ResponseEntity<ApiResponse> registerBatch(@Valid @RequestBody List<RegisterRequest> registerRequests) {
        List<User> registeredUsers = authService.register(registerRequests);
        return ResponseEntity.ok(new ApiResponse(true, "Users registered successfully", registeredUsers));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change user password")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        authService.changePassword(passwordChangeRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send reset password link to user's email")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(new ApiResponse(true, "Reset password link sent to email"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password using token")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the currently authenticated user")
    public ApiResponse logout(@RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }
}