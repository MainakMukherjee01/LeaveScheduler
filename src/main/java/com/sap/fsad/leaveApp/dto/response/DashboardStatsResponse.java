package com.sap.fsad.leaveApp.dto.response;

import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private long totalUsers; // Total number of users in the system
    private long activeUsers; // Number of active users
    private long pendingLeaves; // Total number of pending leave applications
    private Map<UserRole, Long> roleDistribution; // Distribution of users by role
    private List<LeaveApplication> recentLeaveApplications; // List of recent leave applications
}