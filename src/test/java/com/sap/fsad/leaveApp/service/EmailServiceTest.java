package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendLeaveApplicationEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("John Doe");

        emailService.sendLeaveApplicationEmail(null); // Mocked behavior

        verify(mailSender, times(1)).createMimeMessage();
    }
}