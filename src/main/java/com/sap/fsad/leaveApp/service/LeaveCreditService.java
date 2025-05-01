package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.model.LeaveBalance;
import com.sap.fsad.leaveApp.model.LeavePolicy;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.repository.LeaveBalanceRepository;
import com.sap.fsad.leaveApp.repository.LeavePolicyRepository;
import com.sap.fsad.leaveApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveCreditService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    /**
     * Credit annual leave for a specific user
     */
    @Transactional
    public ApiResponse creditAnnualLeave(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LeavePolicy> policies = new ArrayList<>();
        for (UserRole role : user.getRoles()) {
            policies.addAll(leavePolicyRepository.findByApplicableRolesAndActive(role));
        }
        int currentYear = LocalDate.now().getYear();

        for (LeavePolicy policy : policies) {
            creditLeaveForUserAndPolicy(user, policy, currentYear);
        }

        // Notify user
        notificationService.createLeaveCreditedNotification(user);
        emailService.sendLeaveCreditEmail(user);

        return new ApiResponse(true, "Annual leave credited successfully for user: " + user.getFullName());
    }

    /**
     * Credit annual leave for all users
     */
    @Transactional
    public ApiResponse creditAnnualLeaveForAllUsers() {
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        int currentYear = LocalDate.now().getYear();
        List<LeavePolicy> allPolicies = leavePolicyRepository.findByIsActiveTrue();

        for (User user : activeUsers) {
            for (LeavePolicy policy : allPolicies) {
                if (user.getRoles().stream().anyMatch(policy.getApplicableRoles()::contains)) {
                    creditLeaveForUserAndPolicy(user, policy, currentYear);
                    // Notify user
                    notificationService.createLeaveCreditedNotification(user);
                    emailService.sendLeaveCreditEmail(user);
                }
            }
        }

        return new ApiResponse(true, "Annual leave credited successfully for all users");
    }

    /**
     * Scheduled task to run at the beginning of each year
     * to credit annual leave balances
     */
    @Scheduled(cron = "0 0 0 1 1 *") // Run at midnight on January 1st
    @Transactional
    public void scheduledAnnualLeaveCredit() {
        creditAnnualLeaveForAllUsers();
    }

    /**
     * Helper method to credit leave for a specific user and policy
     */
    private void creditLeaveForUserAndPolicy(User user, LeavePolicy policy, int year) {
        // Check if leave balance already exists for this user, leave type and year
        Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(
                user, policy.getLeaveType(), year);

        if (existingBalance.isPresent()) {
            LeaveBalance balance = existingBalance.get();

            // Apply carry forward if allowed
            if (policy.getIsCarryForward() && year > 1) {
                // Get previous year's balance
                Optional<LeaveBalance> prevYearBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(
                        user, policy.getLeaveType(), year - 1);

                if (prevYearBalance.isPresent()) {
                    float carryForwardAmount = prevYearBalance.get().getBalance();

                    // Apply max accumulation limit if set
                    if (policy.getMaxAccumulation() != null &&
                            (balance.getBalance() + carryForwardAmount) > policy.getMaxAccumulation()) {
                        carryForwardAmount = policy.getMaxAccumulation() - balance.getBalance();
                    }

                    if (carryForwardAmount > 0) {
                        balance.setBalance(balance.getBalance() + carryForwardAmount);
                        balance.setUpdatedAt(LocalDateTime.now());
                        leaveBalanceRepository.save(balance);
                    }
                }
            } else {
                // Just add the annual credit
                balance.setBalance(balance.getBalance() + policy.getAnnualCredit());

                // Apply maximum accrual limit
                if (policy.getMaxAccumulation() != null &&
                        balance.getBalance() > policy.getMaxAccumulation()) {
                    balance.setBalance(policy.getMaxAccumulation());
                }

                balance.setUpdatedAt(LocalDateTime.now());
                leaveBalanceRepository.save(balance);
            }
        } else {
            // Create new balance
            LeaveBalance newBalance = new LeaveBalance();
            newBalance.setUser(user);
            newBalance.setLeaveType(policy.getLeaveType());
            newBalance.setBalance(policy.getAnnualCredit());
            newBalance.setUsed(0f);
            newBalance.setYear(year);
            newBalance.setCreatedAt(LocalDateTime.now());
            newBalance.setUpdatedAt(LocalDateTime.now());
            leaveBalanceRepository.save(newBalance);
        }
    }

    /**
     * Add special leave credits to specified users
     */
    @Transactional
    public List<ApiResponse> creditSpecialLeave(List<Long> userIds, LeaveType leaveType, float amount, String reason) {
        List<ApiResponse> responses = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();

        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

                Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(
                        user, leaveType, currentYear);

                if (existingBalance.isPresent()) {
                    LeaveBalance balance = existingBalance.get();
                    balance.setBalance(balance.getBalance() + amount);
                    balance.setUpdatedAt(LocalDateTime.now());
                    leaveBalanceRepository.save(balance);
                } else {
                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setUser(user);
                    newBalance.setLeaveType(leaveType);
                    newBalance.setBalance(amount);
                    newBalance.setUsed(0f);
                    newBalance.setYear(currentYear);
                    newBalance.setCreatedAt(LocalDateTime.now());
                    newBalance.setUpdatedAt(LocalDateTime.now());
                    leaveBalanceRepository.save(newBalance);
                }

                // Notify user
                notificationService.createSpecialLeaveCreditedNotification(user, leaveType, amount, reason);
                emailService.sendSpecialLeaveCreditEmail(user, leaveType, amount, reason);

                responses.add(new ApiResponse(true, "Special leave credited for user: " + user.getFullName()));
            } catch (Exception e) {
                responses.add(new ApiResponse(false,
                        "Failed to credit special leave for user ID: " + userId + ". Error: " + e.getMessage()));
            }
        }

        return responses;
    }
}