package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.LeaveApplicationRequest;
import com.sap.fsad.leaveApp.dto.response.LeaveResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.repository.LeaveApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveServiceTest {

    @InjectMocks
    private LeaveService leaveService;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("John Doe");
    }

    @Test
    void testApplyLeave_Success() {
        when(userService.getCurrentUser()).thenReturn(mockUser);

        LeaveApplicationRequest request = new LeaveApplicationRequest();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(5));
        request.setLeaveType(LeaveType.CASUAL);

        LeaveResponse response = leaveService.applyLeave(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getUsername());
        verify(leaveApplicationRepository, times(1)).save(any(LeaveApplication.class));
    }

    @Test
    void testApplyLeave_InvalidDates() {
        LeaveApplicationRequest request = new LeaveApplicationRequest();
        request.setStartDate(LocalDate.now().plusDays(5));
        request.setEndDate(LocalDate.now().plusDays(1));

        assertThrows(BadRequestException.class, () -> leaveService.applyLeave(request));
    }
}