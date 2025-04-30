package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.model.Notification;
import com.sap.fsad.leaveApp.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "Endpoints for managing notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get current user's notifications")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Notification>> getCurrentUserNotifications() {
        List<Notification> notifications = notificationService.getCurrentUserNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get current user's unread notifications")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Notification>> getCurrentUserUnreadNotifications() {
        List<Notification> notifications = notificationService.getCurrentUserUnreadNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get count of unread notifications")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Long> getCurrentUserUnreadNotificationCount() {
        long count = notificationService.getCurrentUserUnreadNotificationCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> markAllNotificationsAsRead() {
        notificationService.markAllNotificationsAsRead();
        return ResponseEntity.ok(new ApiResponse(true, "All notifications marked as read"));
    }
}