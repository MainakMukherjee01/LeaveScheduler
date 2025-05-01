package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Send email with leave application details to manager
     */
    @Async
    public void sendLeaveApplicationEmail(LeaveApplication leaveApplication) {
        try {
            User employee = leaveApplication.getUser();
            User manager = employee.getManager();

            if (manager == null || manager.getEmail() == null) {
                return; // Skip if manager email is not available
            }

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("managerName", manager.getFullName());
            templateVariables.put("employeeName", employee.getFullName());
            templateVariables.put("leaveType", leaveApplication.getLeaveType());
            templateVariables.put("startDate", leaveApplication.getStartDate());
            templateVariables.put("endDate", leaveApplication.getEndDate());
            templateVariables.put("numberOfDays", leaveApplication.getNumberOfDays());
            templateVariables.put("reason", leaveApplication.getReason());
            templateVariables.put("applicationId", leaveApplication.getId());

            String subject = "Leave Application: " + employee.getFullName();
            String content = processTemplate("leave-application", templateVariables);

            sendEmail(manager.getEmail(), subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send leave application email: {}", e.getMessage());
        }
    }

    /**
     * Send email with leave approval details to employee
     */
    @Async
    public void sendLeaveApprovedEmail(LeaveApplication leaveApplication) {
        try {
            User employee = leaveApplication.getUser();
            User manager = leaveApplication.getApprovedBy();

            if (employee.getEmail() == null) {
                return; // Skip if employee email is not available
            }

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("employeeName", employee.getFullName());
            templateVariables.put("managerName", manager != null ? manager.getFullName() : "Your manager");
            templateVariables.put("leaveType", leaveApplication.getLeaveType());
            templateVariables.put("startDate", leaveApplication.getStartDate());
            templateVariables.put("endDate", leaveApplication.getEndDate());
            templateVariables.put("numberOfDays", leaveApplication.getNumberOfDays());
            templateVariables.put("remarks", leaveApplication.getRemarks());
            templateVariables.put("applicationId", leaveApplication.getId());

            String subject = "Leave Approved: " + leaveApplication.getLeaveType();
            String content = processTemplate("leave-approved", templateVariables);

            sendEmail(employee.getEmail(), subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send leave approval email: {}", e.getMessage());
        }
    }

    /**
     * Send email with leave rejection details to employee
     */
    @Async
    public void sendLeaveRejectedEmail(LeaveApplication leaveApplication) {
        try {
            User employee = leaveApplication.getUser();
            User manager = leaveApplication.getApprovedBy();

            if (employee.getEmail() == null) {
                return; // Skip if employee email is not available
            }

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("employeeName", employee.getFullName());
            templateVariables.put("managerName", manager != null ? manager.getFullName() : "Your manager");
            templateVariables.put("leaveType", leaveApplication.getLeaveType());
            templateVariables.put("startDate", leaveApplication.getStartDate());
            templateVariables.put("endDate", leaveApplication.getEndDate());
            templateVariables.put("numberOfDays", leaveApplication.getNumberOfDays());
            templateVariables.put("remarks", leaveApplication.getRemarks());
            templateVariables.put("applicationId", leaveApplication.getId());

            String subject = "Leave Rejected: " + leaveApplication.getLeaveType();
            String content = processTemplate("leave-rejected", templateVariables);

            sendEmail(employee.getEmail(), subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send leave rejection email: {}", e.getMessage());
        }
    }

    /**
     * Send email when leave is withdrawn
     */
    @Async
    public void sendLeaveWithdrawalEmail(LeaveApplication leaveApplication) {
        try {
            User employee = leaveApplication.getUser();
            User manager = employee.getManager();

            if (manager == null || manager.getEmail() == null) {
                return; // Skip if manager email is not available
            }

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("managerName", manager.getFullName());
            templateVariables.put("employeeName", employee.getFullName());
            templateVariables.put("leaveType", leaveApplication.getLeaveType());
            templateVariables.put("startDate", leaveApplication.getStartDate());
            templateVariables.put("endDate", leaveApplication.getEndDate());
            templateVariables.put("numberOfDays", leaveApplication.getNumberOfDays());
            templateVariables.put("applicationId", leaveApplication.getId());

            String subject = "Leave Application Withdrawn: " + employee.getFullName();
            String content = processTemplate("leave-withdrawal", templateVariables);

            sendEmail(manager.getEmail(), subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send leave withdrawal email: {}", e.getMessage());
        }
    }

    /**
     * Send email notification about annual leave credit
     */
    @Async
    public void sendLeaveCreditEmail(User user) {
        try {
            if (user.getEmail() == null) {
                return; // Skip if user email is not available
            }

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("userName", user.getFullName());
            templateVariables.put("year", java.time.LocalDate.now().getYear());

            String subject = "Annual Leave Credit Notification";
            String content = processTemplate("leave-credit", templateVariables);

            sendEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send leave credit email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Send email notification about special leave credit
     */
    @Async
    public void sendSpecialLeaveCreditEmail(User user, LeaveType leaveType, float amount, String reason) {
        try {
            if (user.getEmail() == null) {
                return; // Skip if user email is not available
            }

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("userName", user.getFullName());
            templateVariables.put("leaveType", leaveType);
            templateVariables.put("amount", amount);
            templateVariables.put("reason", reason);

            String subject = "Special Leave Credit Notification";
            String content = processTemplate("special-leave-credit", templateVariables);

            sendEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send special leave credit email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendResetPasswordEmail(String email, String resetLink) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("resetLink", resetLink);

            String subject = "Reset Your Password";
            String content = processTemplate("reset-password", templateVariables);

            sendEmail(email, subject, content);
        } catch (Exception e) {
            // Log the error but don't propagate - non-critical operation
            logger.error("Failed to send reset password email to {}: {}", email, e.getMessage());
        }
    }

    /**
     * Process the HTML template using Thymeleaf
     */
    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

    /**
     * Send HTML email
     */
    @Retryable(value = MessagingException.class, maxAttempts = 3, backoff = @Backoff(delay = 20000))
    public void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Recover
    public void recover(MessagingException e, String to, String subject, String htmlContent) {
        logger.error("Failed to send email to {} after multiple attempts: {}", to, e.getMessage());
    }
}
