package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.PasswordChangeRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.LeaveBalanceResponse;
import com.sap.fsad.leaveApp.dto.response.UserResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.LeaveBalance;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.repository.LeaveBalanceRepository;
import com.sap.fsad.leaveApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all users by manager ID
     */
    public List<UserResponse> getUsersByManagerId(Long managerId) {
        return userRepository.findByManagerId(managerId).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Change password
     */
    public ApiResponse changePassword(PasswordChangeRequest request) {
        User currentUser = getCurrentUser();

        // Check if current password matches
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Check if new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password don't match");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);

        return new ApiResponse(true, "Password changed successfully");
    }

    /**
     * Get leave balances for current user
     */
    public List<LeaveBalanceResponse> getCurrentUserLeaveBalances() {
        User currentUser = getCurrentUser();
        int currentYear = LocalDateTime.now().getYear();

        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByUserAndYear(currentUser, currentYear);

        return leaveBalances.stream()
                .map(this::convertToLeaveBalanceResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get leave balances for user by ID
     */
    public List<LeaveBalanceResponse> getUserLeaveBalances(Long userId) {
        User user = getUserById(userId);
        int currentYear = LocalDateTime.now().getYear();

        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByUserAndYear(user, currentYear);

        return leaveBalances.stream()
                .map(this::convertToLeaveBalanceResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert User to UserResponse
     */
    public UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles() != null ? user.getRoles() : Collections.emptySet());
        response.setDepartment(user.getDepartment());
        response.setManagerId(user.getManager() != null ? user.getManager().getId() : null);
        response.setManagerName(user.getManager() != null ? user.getManager().getFullName() : null);
        response.setJoiningDate(user.getJoiningDate());
        response.setPhone(user.getPhone());
        response.setEmergencyContact(user.getEmergencyContact());
        response.setActive(user.isActive());
        response.setLastLogin(user.getLastLogin());
        return response;
    }

    /**
     * Convert LeaveBalance to LeaveBalanceResponse
     */
    private LeaveBalanceResponse convertToLeaveBalanceResponse(LeaveBalance leaveBalance) {
        LeaveBalanceResponse response = new LeaveBalanceResponse();
        response.setId(leaveBalance.getId());
        response.setUserId(leaveBalance.getUser().getId());
        response.setLeaveType(leaveBalance.getLeaveType());
        response.setLeaveTypeName(leaveBalance.getLeaveType().toString());
        response.setBalance(leaveBalance.getBalance());
        response.setUsed(leaveBalance.getUsed());
        response.setYear(leaveBalance.getYear());

        return response;
    }
}