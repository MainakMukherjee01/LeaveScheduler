package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.LeaveApplicationRequest;
import com.sap.fsad.leaveApp.dto.response.LeaveResponse;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.LeaveBalance;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.LeavePolicy;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.repository.LeaveApplicationRepository;
import com.sap.fsad.leaveApp.repository.LeaveBalanceRepository;
import com.sap.fsad.leaveApp.repository.LeavePolicyRepository;
import com.sap.fsad.leaveApp.util.DateUtil;
import com.sap.fsad.leaveApp.util.LeaveCalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LeaveCalculator leaveCalculator;

    /**
     * Apply for leave
     */
    @Transactional
    public LeaveResponse applyLeave(LeaveApplicationRequest request) {
        User currentUser = userService.getCurrentUser();

        // Validate leave dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot apply leave for past dates");
        }

        // Calculate number of working days
        int workingDays = leaveCalculator.calculateWorkingDays(request.getStartDate(), request.getEndDate());

        if (workingDays <= 0) {
            throw new BadRequestException("Leave period contains no working days");
        }

        // Check leave policy
        LeavePolicy policy = currentUser.getRoles().stream()
                .map(role -> leavePolicyRepository.findByLeaveTypeAndApplicableRolesContaining(
                        request.getLeaveType(), role))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Leave policy not found for this leave type"));

        // Validate against policy rules
        if (workingDays < policy.getMinDuration()) {
            throw new BadRequestException(
                    "Leave duration is less than minimum required: " + policy.getMinDuration() + " days");
        }

        if (policy.getMaxDuration() != null && workingDays > policy.getMaxDuration()) {
            throw new BadRequestException(
                    "Leave duration exceeds maximum allowed: " + policy.getMaxDuration() + " days");
        }

        // Check notice period
        if (DateUtil.getDaysBetween(LocalDate.now(), request.getStartDate()) < policy.getNoticeRequired()) {
            throw new BadRequestException("Leave application doesn't meet the required notice period of "
                    + policy.getNoticeRequired() + " days");
        }

        // Check leave balance
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(
                currentUser, request.getLeaveType(), LocalDate.now().getYear())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance", "user and type", currentUser.getId()));

        if (leaveBalance.getBalance() < workingDays) {
            throw new BadRequestException("Insufficient leave balance. Available: " + leaveBalance.getBalance()
                    + ", Required: " + workingDays);
        }

        // Check for overlapping leave applications
        if (leaveApplicationRepository.existsOverlappingLeave(
                currentUser.getId(), request.getStartDate(), request.getEndDate(), 
                List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED))) {
            throw new BadRequestException("You already have pending or approved leave during this period");
        }

        // Create and save leave application
        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setUser(currentUser);
        leaveApplication.setStartDate(request.getStartDate());
        leaveApplication.setEndDate(request.getEndDate());
        leaveApplication.setLeaveType(request.getLeaveType());
        leaveApplication.setReason(request.getReason());
        leaveApplication.setContactAddress(request.getContactAddress());
        leaveApplication.setContactPhone(request.getContactPhone());
        leaveApplication.setStatus(LeaveStatus.PENDING);
        leaveApplication.setNumberOfDays(workingDays);
        leaveApplication.setAppliedOn(LocalDateTime.now());
        leaveApplication.setUpdatedAt(LocalDateTime.now());

        if (request.getAttachmentPath() != null) {
            leaveApplication.setAttachmentPath(request.getAttachmentPath());
        }

        LeaveApplication savedApplication = leaveApplicationRepository.save(leaveApplication);

        // Notify manager
        User manager = currentUser.getManager();
        if (manager != null) {
            notificationService.createLeaveApplicationNotification(manager, savedApplication);
            emailService.sendLeaveApplicationEmail(savedApplication);
        }

        return convertToLeaveResponse(savedApplication);
    }

    /**
     * Get leave application by ID
     */
    public LeaveResponse getLeaveById(Long id) {
        User currentUser = userService.getCurrentUser();
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveApplication", "id", id));

        // Check if the user has permission to view this leave
        if (!leaveApplication.getUser().getId().equals(currentUser.getId()) &&
                (currentUser.getManager() == null
                        || !currentUser.getId().equals(leaveApplication.getUser().getManager().getId()))) {
            throw new BadRequestException("You don't have permission to view this leave application");
        }

        return convertToLeaveResponse(leaveApplication);
    }

    /**
     * Get all leave applications for current user
     */
    public List<LeaveResponse> getCurrentUserLeaves() {
        User currentUser = userService.getCurrentUser();
        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findByUserId(currentUser.getId());

        return leaveApplications.stream()
                .map(this::convertToLeaveResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pending leave applications for current user
     */
    public List<LeaveResponse> getCurrentUserPendingLeaves() {
        User currentUser = userService.getCurrentUser();
        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findByUserIdAndStatus(
                currentUser.getId(), LeaveStatus.PENDING);

        return leaveApplications.stream()
                .map(this::convertToLeaveResponse)
                .collect(Collectors.toList());
    }

    /**
     * Withdraw a leave application
     */
    @Transactional
    public ApiResponse withdrawLeave(Long id) {
        User currentUser = userService.getCurrentUser();
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveApplication", "id", id));

        // Check if the leave belongs to the current user
        if (!leaveApplication.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You don't have permission to withdraw this leave application");
        }

        // Check if the leave is in pending status
        if (leaveApplication.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave applications can be withdrawn");
        }

        // Update leave status
        leaveApplication.setStatus(LeaveStatus.WITHDRAWN);
        leaveApplication.setUpdatedAt(LocalDateTime.now());
        leaveApplicationRepository.save(leaveApplication);

        // Notify manager
        User manager = currentUser.getManager();
        if (manager != null) {
            notificationService.createLeaveWithdrawalNotification(manager, leaveApplication);
            emailService.sendLeaveWithdrawalEmail(leaveApplication);
        }

        return new ApiResponse(true, "Leave application withdrawn successfully");
    }

    /**
     * Calculate leave history statistics for current user
     */
    public LeaveResponse.LeaveStats getCurrentUserLeaveStats() {
        User currentUser = userService.getCurrentUser();
        int currentYear = LocalDateTime.now().getYear();

        List<LeaveBalance> balances = leaveBalanceRepository.findByUserAndYear(currentUser, currentYear);

        float totalBalance = 0f;
        float totalUsed = 0f;

        for (LeaveBalance balance : balances) {
            totalBalance += balance.getBalance();
            totalUsed += balance.getUsed();
        }

        int pendingLeaves = leaveApplicationRepository.countByUserIdAndStatus(currentUser.getId(), LeaveStatus.PENDING);

        LeaveResponse.LeaveStats stats = new LeaveResponse.LeaveStats();
        stats.setTotalBalance(totalBalance);
        stats.setTotalUsed(totalUsed);
        stats.setPendingLeaves(pendingLeaves);

        return stats;
    }

    /**
     * Convert LeaveApplication to LeaveResponse
     */
    private LeaveResponse convertToLeaveResponse(LeaveApplication leaveApplication) {
        LeaveResponse response = new LeaveResponse();
        response.setId(leaveApplication.getId());
        response.setUserId(leaveApplication.getUser().getId());
        response.setUsername(leaveApplication.getUser().getFullName());
        response.setStartDate(leaveApplication.getStartDate());
        response.setEndDate(leaveApplication.getEndDate());
        response.setLeaveType(leaveApplication.getLeaveType());
        response.setReason(leaveApplication.getReason());
        response.setContactAddress(leaveApplication.getContactAddress());
        response.setContactPhone(leaveApplication.getContactPhone());
        response.setStatus(leaveApplication.getStatus());
        response.setNumberOfDays(leaveApplication.getNumberOfDays());
        response.setAppliedOn(leaveApplication.getAppliedOn());

        if (leaveApplication.getApprovedBy() != null) {
            response.setApprovedById(leaveApplication.getApprovedBy().getId());
            response.setApprovedBy(leaveApplication.getApprovedBy().getFullName());
            response.setApprovedOn(leaveApplication.getApprovedOn());
        }

        response.setRemarks(leaveApplication.getRemarks());

        return response;
    }
}