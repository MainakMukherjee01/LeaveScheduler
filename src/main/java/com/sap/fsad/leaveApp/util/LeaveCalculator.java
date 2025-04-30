package com.sap.fsad.leaveApp.util;

import com.sap.fsad.leaveApp.model.Holiday;
import com.sap.fsad.leaveApp.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LeaveCalculator {

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * Calculate the number of leave days between two dates,
     * excluding weekends and holidays
     */
    public int calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        // Get all business days between start and end
        List<LocalDate> businessDays = DateUtil.getBusinessDaysBetween(startDate, endDate);

        // Get all holidays between start and end
        List<Holiday> holidays = holidayRepository.findByDateBetween(startDate, endDate);
        List<LocalDate> holidayDates = holidays.stream()
                .map(Holiday::getDate)
                .collect(Collectors.toList());

        // Remove holidays from business days
        businessDays.removeIf(holidayDates::contains);

        return businessDays.size();
    }

    /**
     * Check if a date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByDate(date);
    }

    /**
     * Check if a date is a working day (not weekend and not holiday)
     */
    public boolean isWorkingDay(LocalDate date) {
        return !DateUtil.isWeekend(date) && !isHoliday(date);
    }

    /**
     * Get the next working day from a given date
     */
    public LocalDate getNextWorkingDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (!isWorkingDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    /**
     * Get all days excluding weekends
     */
    public int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek().getValue() < 6) { // Monday to Friday
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return workingDays;
    }
}