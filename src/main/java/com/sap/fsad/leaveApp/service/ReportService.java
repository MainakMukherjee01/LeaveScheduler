package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.sap.fsad.leaveApp.model.Holiday;
import com.sap.fsad.leaveApp.repository.LeaveApplicationRepository;
import com.sap.fsad.leaveApp.repository.HolidayRepository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * Get leave usage report for all users
     */
    public List<LeaveApplication> getLeaveUsageReport() {
        return leaveApplicationRepository.findAll();
    }

    /**
     * Get pending approvals report
     */
    public List<LeaveApplication> getPendingApprovalsReport() {
        return leaveApplicationRepository.findByStatus(LeaveStatus.PENDING);
    }

    /**
     * Get holiday schedule report
     */
    public List<Holiday> getHolidayScheduleReport() {
        return holidayRepository.findAll();
    }

    /**
     * Export leave usage report to Excel
     */
    public byte[] exportLeaveUsageToExcel(List<LeaveApplication> leaveApplications) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Leave Usage");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Employee");
        headerRow.createCell(1).setCellValue("Leave Type");
        headerRow.createCell(2).setCellValue("Start Date");
        headerRow.createCell(3).setCellValue("End Date");
        headerRow.createCell(4).setCellValue("Status");

        // Data rows
        int rowNum = 1;
        for (LeaveApplication leave : leaveApplications) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(leave.getUser().getFullName());
            row.createCell(1).setCellValue(leave.getLeaveType().toString());
            row.createCell(2).setCellValue(leave.getStartDate().toString());
            row.createCell(3).setCellValue(leave.getEndDate().toString());
            row.createCell(4).setCellValue(leave.getStatus().toString());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    /**
     * Export leave usage report to PDF
     */
    public byte[] exportLeaveUsageToPDF(List<LeaveApplication> leaveApplications) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        document.add(new Paragraph("Leave Usage Report"));
        for (LeaveApplication leave : leaveApplications) {
            document.add(new Paragraph(leave.getUser().getFullName() + " - " +
                    leave.getLeaveType() + " - " +
                    leave.getStartDate() + " to " + leave.getEndDate() + " - " +
                    leave.getStatus()));
        }

        document.close();
        return out.toByteArray();
    }
}