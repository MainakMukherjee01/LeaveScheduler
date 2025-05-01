package com.sap.fsad.leaveApp.dto.response;

import java.time.LocalDateTime;

public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public CustomErrorResponse(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}