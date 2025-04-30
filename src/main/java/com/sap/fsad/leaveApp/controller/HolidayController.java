package com.sap.fsad.leaveApp.controller;

import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.model.Holiday;
import com.sap.fsad.leaveApp.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@Tag(name = "Holiday Management", description = "Endpoints for managing holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @PostMapping
    @Operation(summary = "Create a new holiday (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Holiday> createHoliday(@Valid @RequestBody Holiday holiday) {
        Holiday createdHoliday = holidayService.createHoliday(holiday);
        return ResponseEntity.ok(createdHoliday);
    }

    @GetMapping("/")
    @Operation(summary = "Get all holidays")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        List<Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get holiday by ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable Long id) {
        Holiday holiday = holidayService.getHolidayById(id);
        return ResponseEntity.ok(holiday);
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Get holidays by year")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Holiday>> getHolidaysByYear(@PathVariable Integer year) {
        List<Holiday> holidays = holidayService.getHolidaysByYear(year);
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/month/{month}/year/{year}")
    @Operation(summary = "Get holidays by month and year")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Holiday>> getHolidaysByMonthAndYear(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        List<Holiday> holidays = holidayService.getHolidaysByMonthAndYear(month, year);
        return ResponseEntity.ok(holidays);
    }

    @PutMapping("//{id:\\\\d+}")
    @Operation(summary = "Update a holiday (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Holiday> updateHoliday(
            @PathVariable Long id,
            @Valid @RequestBody Holiday holidayDetails) {
        Holiday updatedHoliday = holidayService.updateHoliday(id, holidayDetails);
        return ResponseEntity.ok(updatedHoliday);
    }

    @DeleteMapping("//{id:\\\\d+}")
    @Operation(summary = "Delete a holiday (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> deleteHoliday(@PathVariable Long id) {
        ApiResponse response = holidayService.deleteHoliday(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get calendar view of holidays")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Holiday>> getCalendarView() {
        List<Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }
}