package com.sap.fsad.leaveApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEventResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private String userName;
    private String title;
    private String eventType;
}