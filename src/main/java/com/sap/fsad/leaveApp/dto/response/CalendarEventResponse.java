package com.sap.fsad.leaveApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CalendarEventResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private String userName; // Null for holidays
    private String title; // Leave type or holiday name
    private String eventType; // "Leave" or "Holiday"
}