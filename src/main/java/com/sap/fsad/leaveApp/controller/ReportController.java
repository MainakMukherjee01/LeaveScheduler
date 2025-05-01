package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.model.LeaveApplication;
import org.springframework.http.MediaType;
import com.sap.fsad.leaveApp.model.Holiday;
import com.sap.fsad.leaveApp.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Endpoints for generating and exporting reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/leave-usage")
    @Operation(summary = "Get leave usage report")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveApplication>> getLeaveUsageReport() {
        List<LeaveApplication> report = reportService.getLeaveUsageReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/pending-approvals")
    @Operation(summary = "Get pending approvals report")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<LeaveApplication>> getPendingApprovalsReport() {
        List<LeaveApplication> report = reportService.getPendingApprovalsReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/holiday-schedule")
    @Operation(summary = "Get holiday schedule report")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Holiday>> getHolidayScheduleReport() {
        List<Holiday> report = reportService.getHolidayScheduleReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/leave-usage/export/excel")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export leave usage report to Excel")
    public ResponseEntity<byte[]> exportLeaveUsageToExcel() throws Exception {
        List<LeaveApplication> report = reportService.getLeaveUsageReport();
        byte[] excelData = reportService.exportLeaveUsageToExcel(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leave-usage.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    @GetMapping("/leave-usage/export/pdf")
    @Operation(summary = "Export leave usage report to PDF")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<byte[]> exportLeaveUsageToPDF() throws Exception {
        List<LeaveApplication> report = reportService.getLeaveUsageReport();
        byte[] pdfData = reportService.exportLeaveUsageToPDF(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leave-usage.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}