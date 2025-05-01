package com.sap.fsad.leaveApp.util;

import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.LeaveBalance;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplates {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Get email context for leave application
     */
    public static Map<String, Object> getLeaveApplicationContext(LeaveApplication application, User user) {
        Map<String, Object> context = new HashMap<>();
        context.put("userName", user.getFullName());
        context.put("leaveType", application.getLeaveType().toString());
        context.put("startDate", application.getStartDate().format(DATE_FORMATTER));
        context.put("endDate", application.getEndDate().format(DATE_FORMATTER));
        context.put("numberOfDays", application.getNumberOfDays());
        context.put("reason", application.getReason());
        context.put("applicationId", application.getId());
        context.put("dateApplied", application.getAppliedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return context;
    }

    /**
     * Get email context for leave status update
     */
    public static Map<String, Object> getLeaveStatusUpdateContext(LeaveApplication application, User user) {
        Map<String, Object> context = getLeaveApplicationContext(application, user);
        context.put("status", application.getStatus().toString());

        if (application.getStatus() == LeaveStatus.APPROVED) {
            context.put("approvedBy", application.getApprovedBy().getFullName());
            context.put("approvedOn",
                    application.getApprovedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        }

        if (application.getRemarks() != null && !application.getRemarks().isEmpty()) {
            context.put("remarks", application.getRemarks());
        }

        return context;
    }

    /**
     * Get email context for leave balance credit
     */
    public static Map<String, Object> getLeaveBalanceCreditContext(User user, LeaveBalance leaveBalance) {
        Map<String, Object> context = new HashMap<>();
        context.put("userName", user.getFullName());
        context.put("leaveType", leaveBalance.getLeaveType().toString());
        context.put("creditedAmount", leaveBalance.getBalance());
        context.put("year", leaveBalance.getYear());
        context.put("totalBalance", leaveBalance.getBalance());
        return context;
    }

    /**
     * Get subject for leave application
     */
    public static String getLeaveApplicationSubject(LeaveApplication application) {
        return "Leave Application - " + application.getLeaveType().toString();
    }

    /**
     * Get subject for leave status update
     */
    public static String getLeaveStatusUpdateSubject(LeaveApplication application) {
        return "Leave Application " + application.getStatus().toString() + " - "
                + application.getLeaveType().toString();
    }

    /**
     * Get subject for leave balance credit
     */
    public static String getLeaveBalanceCreditSubject(LeaveBalance leaveBalance) {
        return leaveBalance.getLeaveType().toString() + " Leave Credited for " + leaveBalance.getYear();
    }
}