package com.sap.fsad.leaveApp;

import com.sap.fsad.leaveApp.dto.request.LeaveApplicationRequest;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LeaveWorkflowIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testLeaveApplicationWorkflow() {
        User user = userRepository.findById(1L).orElseThrow();
        assertNotNull(user);

        LeaveApplicationRequest request = new LeaveApplicationRequest();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(5));
        request.setLeaveType(LeaveType.CASUAL);

        // Simulate leave application
        // Simulate approval/rejection
        // Validate notifications
    }
}