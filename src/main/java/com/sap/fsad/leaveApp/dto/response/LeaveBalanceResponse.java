package com.sap.fsad.leaveApp.dto.response;

import com.sap.fsad.leaveApp.model.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceResponse {
    private Long id;
    private Long userId;
    private LeaveType leaveType;
    private Float balance;
    private Float used;
    private Integer year;
    private String leaveTypeName;
}