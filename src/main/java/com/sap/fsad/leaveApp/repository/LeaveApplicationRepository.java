package com.sap.fsad.leaveApp.repository;

import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
        List<LeaveApplication> findByStatus(LeaveStatus status);

        List<LeaveApplication> findByUserId(Long userId);

        List<LeaveApplication> findByUser(User user);

        List<LeaveApplication> findByUserIdAndStatus(Long userId, LeaveStatus status);

        List<LeaveApplication> findByUserAndStatus(User user, LeaveStatus status);

        List<LeaveApplication> findByUserAndLeaveType(User user, LeaveType leaveType);

        List<LeaveApplication> findByUserManagerIdAndStatus(Long managerId, LeaveStatus status);

        @Query("SELECT la FROM LeaveApplication la WHERE la.user.manager.id = :managerId AND la.status = :status")
        List<LeaveApplication> findByManagerIdAndStatus(@Param("manager_id") Long managerId,
                        @Param("status") LeaveStatus status);

        @Query("SELECT la FROM LeaveApplication la WHERE la.startDate <= :endDate AND la.endDate >= :startDate AND la.user.id = :userId")
        List<LeaveApplication> findOverlappingLeaves(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("userId") Long userId);

        @Query("SELECT la FROM LeaveApplication la WHERE la.startDate BETWEEN :startDate AND :endDate OR la.endDate BETWEEN :startDate AND :endDate")
        List<LeaveApplication> findLeavesInDateRange(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT SUM(la.numberOfDays) FROM LeaveApplication la WHERE la.user.id = :userId AND la.leaveType = :leaveType AND la.status = 'APPROVED' AND YEAR(la.startDate) = :year")
        Float countApprovedLeavesByUserAndTypeAndYear(@Param("userId") Long userId,
                        @Param("leaveType") LeaveType leaveType,
                        @Param("year") int year);

        @Query("SELECT COUNT(l) FROM LeaveApplication l WHERE l.user.id = :userId AND l.status = :status")
        int countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") LeaveStatus status);

        @Query("SELECT CASE WHEN COUNT(la) > 0 THEN true ELSE false END " +
                        "FROM LeaveApplication la " +
                        "WHERE la.user.id = :userId " +
                        "AND la.status IN :statuses " +
                        "AND (la.startDate <= :endDate AND la.endDate >= :startDate)")
        boolean existsOverlappingLeave(@Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("statuses") List<LeaveStatus> statuses);

        @Query("SELECT la FROM LeaveApplication la WHERE la.status = 'PENDING' AND la.appliedOn <= :timeoutThreshold")
        List<LeaveApplication> findPendingLeavesBefore(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

        @Query("SELECT l FROM LeaveApplication l WHERE l.user.department = :department")
        List<LeaveApplication> findByUserDepartment(@Param("department") String department);
}
