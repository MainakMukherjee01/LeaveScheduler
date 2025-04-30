package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.LeavePolicyRequest;
import com.sap.fsad.leaveApp.dto.response.DashboardStatsResponse;
import com.sap.fsad.leaveApp.dto.request.UserUpdateRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.UserResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.AuditLog;
import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.LeavePolicy;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import com.sap.fsad.leaveApp.repository.AuditLogRepository;
import com.sap.fsad.leaveApp.repository.LeaveApplicationRepository;
import com.sap.fsad.leaveApp.repository.LeavePolicyRepository;
import com.sap.fsad.leaveApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public DashboardStatsResponse getDashboardStats() {
        User currentUser = userService.getCurrentUser();

        // Verify admin role
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to access admin dashboard");
        }

        DashboardStatsResponse stats = new DashboardStatsResponse();

        // Total users count
        stats.setTotalUsers(userRepository.count());

        // Active users count
        stats.setActiveUsers(userRepository.findByIsActiveTrue().size());

        // Total pending leaves
        stats.setPendingLeaves(leaveApplicationRepository.countByUserIdAndStatus(null, LeaveStatus.PENDING));

        // Role distribution
        Map<UserRole, Long> roleDistribution = new HashMap<>();
        for (UserRole role : UserRole.values()) {
            roleDistribution.put(role, (long) userRepository.findByRole(role).size());
        }
        stats.setRoleDistribution(roleDistribution);

        // Recent leave applications
        List<LeaveApplication> recentLeaves = leaveApplicationRepository.findAll().stream()
                .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
        stats.setRecentLeaveApplications(recentLeaves);

        return stats;
    }

    private void logAdminAction(String action, String details) {
        User currentUser = userService.getCurrentUser();
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetails(details);
        log.setAdminId(currentUser.getId());
        log.setActionTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    /**
     * Get all users (admin only)
     */
    public List<UserResponse> getAllUsers() {
        User currentUser = userService.getCurrentUser();

        // Verify admin role
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to view all users");
        }

        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user details (admin only)
     */
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User currentUser = userService.getCurrentUser();

        // Verify admin role
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to update user details");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Update user fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            // Check if email is already in use
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }

        if (request.getRoles() != null) {

            if (!request.getRoles().contains(UserRole.ADMIN) && !currentUser.getRoles().contains(UserRole.ADMIN)) {
                throw new BadRequestException("You don't have permission to assign this role");
            }
            user.setRoles(request.getRoles());
        }

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", request.getManagerId()));

            // Validate manager role - must be MANAGER or higher
            if (manager.getRoles().contains(UserRole.EMPLOYEE)) {
                throw new BadRequestException("Manager must have MANAGER role or higher");
            }

            user.setManager(manager);
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        logAdminAction("UPDATE_USER_DETAILS",
                "USER ID: " + updatedUser.getId());

        return convertToUserResponse(updatedUser);
    }

    /**
     * Create or update leave policy
     */
    @Transactional
    public LeavePolicy createOrUpdateLeavePolicy(LeavePolicyRequest request) {
        User currentUser = userService.getCurrentUser();

        // Verify admin role
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to manage leave policies");
        }

        LeavePolicy leavePolicy;

        // Create new or update existing policy
        if (request.getId() != null) {
            leavePolicy = leavePolicyRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeavePolicy", "id", request.getId()));
        } else {
            leavePolicy = new LeavePolicy();
            leavePolicy.setCreatedAt(LocalDateTime.now());
        }

        // Update fields
        leavePolicy.setLeaveType(request.getLeaveType());
        leavePolicy.setDescription(request.getDescription());
        leavePolicy.setAnnualCredit(request.getAnnualCredit());
        leavePolicy.setMinDuration(request.getMinDuration());
        leavePolicy.setMaxDuration(request.getMaxDuration());
        leavePolicy.setNoticeRequired(request.getNoticeRequired());
        leavePolicy.setIsCarryForward(request.getIsCarryForward());
        leavePolicy.setMaxAccumulation(request.getMaxAccumulation());
        leavePolicy.setApplicableRoles(request.getApplicableRoles());
        leavePolicy.setIsActive(request.getIsActive());
        leavePolicy.setUpdatedAt(LocalDateTime.now());

        LeavePolicy savedPolicy = leavePolicyRepository.save(leavePolicy);

        logAdminAction(request.getId() == null ? "CREATE_LEAVE_POLICY" : "UPDATE_LEAVE_POLICY",
                "Policy ID: " + savedPolicy.getId() + ", Type: " + savedPolicy.getLeaveType());

        return savedPolicy;
    }

    /**
     * Get leave policy by ID
     */
    public LeavePolicy getLeavePolicyById(Long id) {
        User currentUser = userService.getCurrentUser();

        // Verify admin role for detailed access
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to view leave policy details");
        }

        return leavePolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeavePolicy", "id", id));
    }

    /**
     * Get all leave policies
     */
    public List<LeavePolicy> getAllLeavePolicies() {
        User currentUser = userService.getCurrentUser();

        // Verify admin role for full list
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to view all leave policies");
        }

        return leavePolicyRepository.findAll();
    }

    /**
     * Delete leave policy
     */
    @Transactional
    public ApiResponse deleteLeavePolicy(Long id) {
        User currentUser = userService.getCurrentUser();

        // Verify admin role
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to delete leave policies");
        }

        LeavePolicy leavePolicy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeavePolicy", "id", id));

        // Check if there are any leave balances using this policy
        if (!leaveApplicationRepository.findAll().isEmpty()) {
            // Instead of deleting, just mark as inactive
            leavePolicy.setIsActive(false);
            leavePolicy.setUpdatedAt(LocalDateTime.now());
            leavePolicyRepository.save(leavePolicy);
            return new ApiResponse(true, "Leave policy marked as inactive successfully");
        }

        logAdminAction("DELETE_LEAVE_POLICY",
                "Policy ID: " + leavePolicy.getId() + ", Type: " + leavePolicy.getLeaveType());

        leavePolicyRepository.delete(leavePolicy);
        return new ApiResponse(true, "Leave policy deleted successfully");
    }

    /**
     * Convert User to UserResponse
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setDepartment(user.getDepartment());
        response.setRoles(user.getRoles());
        response.setJoiningDate(user.getJoiningDate());
        response.setEmergencyContact(user.getEmergencyContact());
        response.setPhone(user.getPhone());
        response.setLastLogin(user.getLastLogin());

        if (user.getManager() != null) {
            response.setManagerId(user.getManager().getId());
            response.setManagerName(user.getManager().getFullName());
        }

        response.setActive(user.isActive());

        return response;
    }
}