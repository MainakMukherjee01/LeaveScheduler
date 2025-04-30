package com.sap.fsad.leaveApp.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    /**
     * Calculate business days between two dates (excluding weekends)
     */
    public static long calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        // Ensure start date is before or equal to end date
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        // Count all days between the dates (inclusive of start and end)
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Count weekends
        long weekends = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(totalDays)
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .count();

        return totalDays - weekends;
    }

    /**
     * Check if a date is a weekend (Saturday or Sunday)
     */
    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * Get all dates between two dates (inclusive)
     */
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        // Ensure start date is before or equal to end date
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
    }

    /**
     * Get all business days between two dates (excluding weekends)
     */
    public static List<LocalDate> getBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> allDates = getDatesBetween(startDate, endDate);
        return allDates.stream()
                .filter(date -> !isWeekend(date))
                .collect(Collectors.toList());
    }

    /**
     * Check if two date ranges overlap
     */
    public static boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    /**
     * Get the number of days between two dates
     */

    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
}