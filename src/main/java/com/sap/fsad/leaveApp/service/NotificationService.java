package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.Notification;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.model.enums.NotificationType;
import com.sap.fsad.leaveApp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a notification for a leave application
     */
    @Transactional
    public Notification createLeaveApplicationNotification(User user, LeaveApplication leaveApplication) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.LEAVE_APPLICATION);
        notification.setTitle("New Leave Application");
        notification.setMessage(leaveApplication.getUser().getFullName() + " has applied for " +
                leaveApplication.getNumberOfDays() + " days of " + leaveApplication.getLeaveType() + " leave");
        notification.setIsRead(false);
        notification.setRelatedEntityId(leaveApplication.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Create a notification for leave approval
     */
    @Transactional
    public Notification createLeaveApprovedNotification(User user, LeaveApplication leaveApplication) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.LEAVE_APPROVED);
        notification.setTitle("Leave Approved");
        notification.setMessage("Your application for " + leaveApplication.getNumberOfDays() +
                " days of " + leaveApplication.getLeaveType() + " leave has been approved");
        notification.setIsRead(false);
        notification.setRelatedEntityId(leaveApplication.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Create a notification for leave rejection
     */
    @Transactional
    public Notification createLeaveRejectedNotification(User user, LeaveApplication leaveApplication) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.LEAVE_REJECTED);
        notification.setTitle("Leave Rejected");
        notification.setMessage("Your application for " + leaveApplication.getNumberOfDays() +
                " days of " + leaveApplication.getLeaveType() + " leave has been rejected");
        notification.setIsRead(false);
        notification.setRelatedEntityId(leaveApplication.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Create a notification for leave withdrawal
     */
    @Transactional
    public Notification createLeaveWithdrawalNotification(User user, LeaveApplication leaveApplication) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.LEAVE_WITHDRAWN);
        notification.setTitle("Leave Application Withdrawn");
        notification.setMessage(leaveApplication.getUser().getFullName() + " has withdrawn their application for " +
                leaveApplication.getNumberOfDays() + " days of " + leaveApplication.getLeaveType() + " leave");
        notification.setIsRead(false);
        notification.setRelatedEntityId(leaveApplication.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Create a notification for leave credit
     */
    @Transactional
    public Notification createLeaveCreditedNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.LEAVE_CREDIT);
        notification.setTitle("Leave Balance Updated");
        notification.setMessage("Your annual leave balance has been updated. Please check your balance.");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Create a notification for special leave credit
     */
    @Transactional
    public Notification createSpecialLeaveCreditedNotification(User user, LeaveType leaveType, float amount,
            String reason) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.LEAVE_CREDIT);
        notification.setTitle("Special Leave Credited");
        notification.setMessage(
                amount + " days of " + leaveType + " leave has been credited to your account. Reason: " + reason);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Get all notifications for current user
     */
    public List<Notification> getCurrentUserNotifications() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    /**
     * Get unread notifications for current user
     */
    public List<Notification> getCurrentUserUnreadNotifications() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByUserAndIsReadFalse(currentUser);
    }

    /**
     * Get unread notification count for current user
     */
    public long getCurrentUserUnreadNotificationCount() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.countUnreadNotificationsByUserId(currentUser.getId());
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public Notification markNotificationAsRead(Long id) {
        User currentUser = userService.getCurrentUser();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        // Ensure the notification belongs to the current user
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }

        notification.setIsRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for current user
     */
    @Transactional
    public void markAllNotificationsAsRead() {
        User currentUser = userService.getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(currentUser);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setUpdatedAt(LocalDateTime.now());
        }

        notificationRepository.saveAll(unreadNotifications);
    }
}