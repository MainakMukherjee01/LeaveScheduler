package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.LeaveApprovalRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.LeaveResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.AuditLog;
import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.LeaveBalance;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.repository.AuditLogRepository;
import com.sap.fsad.leaveApp.repository.LeaveApplicationRepository;
import com.sap.fsad.leaveApp.repository.LeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveApprovalService {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Get all pending leave applications for approval
     */
    public List<LeaveResponse> getPendingLeaveApplications() {
        User currentUser = userService.getCurrentUser();
        List<LeaveApplication> pendingApplications = leaveApplicationRepository.findByUserManagerIdAndStatus(
                currentUser.getId(), LeaveStatus.PENDING);

        return pendingApplications.stream()
                .map(this::convertToLeaveResponse)
                .collect(Collectors.toList());
    }

    /**
     * Approve a leave application
     */
    @Transactional
    public ApiResponse approveLeave(Long id, LeaveApprovalRequest request) {
        User currentUser = userService.getCurrentUser();
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveApplication", "id", id));

        // Check if the current user is the manager of the leave applicant
        if (leaveApplication.getUser().getManager() == null ||
                !leaveApplication.getUser().getManager().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You don't have permission to approve this leave application");
        }

        // Check if the leave is in pending status
        if (leaveApplication.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave applications can be approved");
        }

        // Check if the leave has not already started
        if (leaveApplication.getStartDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new BadRequestException("Cannot approve leave that has already started");
        }

        // Update leave status
        leaveApplication.setStatus(LeaveStatus.APPROVED);
        leaveApplication.setApprovedBy(currentUser);
        leaveApplication.setApprovedOn(LocalDateTime.now());
        leaveApplication.setRemarks(request.getRemarks());
        leaveApplication.setUpdatedAt(LocalDateTime.now());
        leaveApplicationRepository.save(leaveApplication);

        // Update leave balance
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(
                leaveApplication.getUser(),
                leaveApplication.getLeaveType(),
                leaveApplication.getStartDate().getYear())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance", "user and type",
                        leaveApplication.getUser().getId()));

        float newBalance = leaveBalance.getBalance() - leaveApplication.getNumberOfDays();
        float newUsed = leaveBalance.getUsed() + leaveApplication.getNumberOfDays();

        leaveBalance.setBalance(newBalance);
        leaveBalance.setUsed(newUsed);
        leaveBalance.setUpdatedAt(LocalDateTime.now());
        leaveBalanceRepository.save(leaveBalance);

        // Log the action
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(currentUser.getId());
        auditLog.setLeaveApplication(leaveApplication);
        auditLog.setAction(LeaveStatus.APPROVED.toString());
        auditLog.setDetails(request.getRemarks());
        auditLog.setActionTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        // Notify employee
        notificationService.createLeaveApprovedNotification(leaveApplication.getUser(), leaveApplication);
        emailService.sendLeaveApprovedEmail(leaveApplication);

        return new ApiResponse(true, "Leave application approved successfully");
    }

    /**
     * Reject a leave application
     */
    @Transactional
    public ApiResponse rejectLeave(Long id, LeaveApprovalRequest request) {
        User currentUser = userService.getCurrentUser();
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveApplication", "id", id));

        // Check if the current user is the manager of the leave applicant
        if (leaveApplication.getUser().getManager() == null ||
                !leaveApplication.getUser().getManager().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You don't have permission to reject this leave application");
        }

        // Check if the leave is in pending status
        if (leaveApplication.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave applications can be rejected");
        }

        // Update leave status
        leaveApplication.setStatus(LeaveStatus.REJECTED);
        leaveApplication.setApprovedBy(currentUser);
        leaveApplication.setApprovedOn(LocalDateTime.now());
        leaveApplication.setRemarks(request.getRemarks());
        leaveApplication.setUpdatedAt(LocalDateTime.now());
        leaveApplicationRepository.save(leaveApplication);

        // Log the action
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(currentUser.getId());
        auditLog.setLeaveApplication(leaveApplication);
        auditLog.setAction(LeaveStatus.REJECTED.toString());
        auditLog.setDetails(request.getRemarks());
        auditLog.setActionTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        // Notify employee
        notificationService.createLeaveRejectedNotification(leaveApplication.getUser(), leaveApplication);
        emailService.sendLeaveRejectedEmail(leaveApplication);

        return new ApiResponse(true, "Leave application rejected successfully");
    }

    /**
     * Get all approved leave applications
     */
    public List<LeaveResponse> getApprovedLeaveApplications() {
        User currentUser = userService.getCurrentUser();
        List<LeaveApplication> approvedApplications = leaveApplicationRepository.findByUserManagerIdAndStatus(
                currentUser.getId(), LeaveStatus.APPROVED);

        return approvedApplications.stream()
                .map(this::convertToLeaveResponse)
                .collect(Collectors.toList());
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

    @Value("${leave.auto-approval.timeout-hours:48}") // Default timeout is 48 hours
    private int autoApprovalTimeoutHours;

    /**
     * Scheduled task to automatically approve pending leave applications after
     * timeout
     */
    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    @Transactional
    public void autoApprovePendingLeaves() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusHours(autoApprovalTimeoutHours);

        List<LeaveApplication> pendingLeaves = leaveApplicationRepository.findPendingLeavesBefore(timeoutThreshold);

        for (LeaveApplication leave : pendingLeaves) {
            leave.setStatus(LeaveStatus.APPROVED);
            leave.setApprovedBy(null); // No manager approved it
            leave.setApprovedOn(LocalDateTime.now());
            leaveApplicationRepository.save(leave);

            // Update leave balance
            LeaveBalance leaveBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(
                    leave.getUser(), leave.getLeaveType(), leave.getStartDate().getYear())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance", "user and type",
                            leave.getUser().getId()));

            leaveBalance.setBalance(leaveBalance.getBalance() - leave.getNumberOfDays());
            leaveBalance.setUsed(leaveBalance.getUsed() + leave.getNumberOfDays());
            leaveBalanceRepository.save(leaveBalance);

            // Notify user
            notificationService.createLeaveApprovedNotification(leave.getUser(), leave);
            emailService.sendLeaveApprovedEmail(leave);
        }
    }
}