package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.Holiday;
import com.sap.fsad.leaveApp.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * Create a new holiday
     */
    @Transactional
    public Holiday createHoliday(Holiday holiday) {
        // Check if holiday already exists on this date
        if (holidayRepository.existsByDate(holiday.getDate())) {
            throw new BadRequestException("A holiday already exists on this date");
        }

        holiday.setCreatedAt(LocalDateTime.now());
        holiday.setUpdatedAt(LocalDateTime.now());
        return holidayRepository.save(holiday);
    }

    /**
     * Get all holidays
     */
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    /**
     * Get holiday by ID
     */
    public Holiday getHolidayById(Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday", "id", id));
    }

    /**
     * Get holidays by year
     */
    public List<Holiday> getHolidaysByYear(Integer year) {
        return holidayRepository.findByYear(year);
    }

    /**
     * Get holidays by month and year
     */
    public List<Holiday> getHolidaysByMonthAndYear(Integer month, Integer year) {
        return holidayRepository.findByMonthAndYear(month, year);
    }

    /**
     * Get holidays between dates
     */
    public List<Holiday> getHolidaysBetweenDates(LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findByDateBetween(startDate, endDate);
    }

    /**
     * Update a holiday
     */
    @Transactional
    public Holiday updateHoliday(Long id, Holiday holidayDetails) {
        Holiday holiday = getHolidayById(id);

        // Check if the new date conflicts with existing holidays
        if (!holiday.getDate().isEqual(holidayDetails.getDate()) &&
                holidayRepository.existsByDate(holidayDetails.getDate())) {
            throw new BadRequestException("A holiday already exists on this date");
        }

        holiday.setName(holidayDetails.getName());
        holiday.setDate(holidayDetails.getDate());
        holiday.setDescription(holidayDetails.getDescription());
        holiday.setUpdatedAt(LocalDateTime.now());

        return holidayRepository.save(holiday);
    }

    /**
     * Delete a holiday
     */
    @Transactional
    public ApiResponse deleteHoliday(Long id) {
        Holiday holiday = getHolidayById(id);
        holidayRepository.delete(holiday);
        return new ApiResponse(true, "Holiday deleted successfully");
    }

    /**
     * Check if a date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByDate(date);
    }
}