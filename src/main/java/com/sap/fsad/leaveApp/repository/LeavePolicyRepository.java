package com.sap.fsad.leaveApp.repository;

import com.sap.fsad.leaveApp.model.LeavePolicy;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {
    Optional<LeavePolicy> findByLeaveType(LeaveType leaveType);

    List<LeavePolicy> findByIsActiveTrue();

    @Query("SELECT lp FROM LeavePolicy lp JOIN lp.applicableRoles ar WHERE ar = :role AND lp.isActive = true")
    List<LeavePolicy> findByApplicableRolesAndActive(@Param("role") UserRole role);

    Optional<LeavePolicy> findByLeaveTypeAndApplicableRolesContaining(LeaveType leaveType, UserRole userRole);
}