package com.sap.fsad.leaveApp.dto.request;

import com.sap.fsad.leaveApp.model.enums.LeaveType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplicationRequest {
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull
    private LeaveType leaveType;

    @NotBlank
    @Size(max = 255)
    private String reason;

    @Size(max = 255)
    private String contactAddress;

    @Size(max = 20)
    private String contactPhone;

    private String attachmentPath;
}