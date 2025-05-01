package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class LeaveCreditServiceTest {

    @InjectMocks
    private LeaveCreditService leaveCreditService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreditAnnualLeaveForAllUsers() {
        leaveCreditService.creditAnnualLeaveForAllUsers();
        verify(userRepository, times(1)).findByIsActiveTrue();
    }
}