package com.sap.fsad.leaveApp.dto.response;

import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponse {
    private Long id;
    private Long userId;
    private String username;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private String reason;
    private String contactAddress;
    private String contactPhone;
    private LeaveStatus status;
    private LocalDateTime appliedOn;
    private String approvedBy;
    private Long approvedById;
    private LocalDateTime approvedOn;
    private String remarks;
    private Integer numberOfDays;
    private String attachmentPath;

    public static class LeaveStats {
        private float totalBalance;
        private float totalUsed;
        private int pendingLeaves;

        public float getTotalBalance() {
            return totalBalance;
        }

        public void setTotalBalance(float totalBalance) {
            this.totalBalance = totalBalance;
        }

        public float getTotalUsed() {
            return totalUsed;
        }

        public void setTotalUsed(float totalUsed) {
            this.totalUsed = totalUsed;
        }

        public int getPendingLeaves() {
            return pendingLeaves;
        }

        public void setPendingLeaves(int pendingLeaves) {
            this.pendingLeaves = pendingLeaves;
        }
    }
}