package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.LoginRequest;
import com.sap.fsad.leaveApp.dto.request.PasswordChangeRequest;
import com.sap.fsad.leaveApp.dto.request.RegisterRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.JwtResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.exception.ResourceNotFoundException;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import com.sap.fsad.leaveApp.repository.LeaveBalanceRepository;
import com.sap.fsad.leaveApp.repository.LeavePolicyRepository;
import com.sap.fsad.leaveApp.repository.UserRepository;
import com.sap.fsad.leaveApp.security.JwtTokenProvider;

import jakarta.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeavePolicyRepository leavePolicyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private EmailService emailService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword");
        mockUser.setEmail("test@example.com");
        mockUser.setRoles(Set.of(UserRole.EMPLOYEE));
        mockUser.setActive(true);
        mockUser.setFailedLoginAttempts(0);
    }

    @Test
void testLogin_Success() {
    // Arrange
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("testuser");
    loginRequest.setPassword("password");

    Authentication authentication = mock(Authentication.class);

    // Mock user repository to return the mock user
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

    // Mock authentication manager to return a valid authentication object
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

    // Mock token provider to return a JWT token
    when(tokenProvider.generateToken(authentication)).thenReturn("jwtToken");

    // Act
    JwtResponse response = authService.login(loginRequest);

    // Assert
    assertNotNull(response);
    assertEquals("jwtToken", response.getToken());
    verify(userRepository, times(1)).save(mockUser);
}

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        doThrow(new BadRequestException("Invalid username or password")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("Password@123");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setRoles(Set.of(UserRole.EMPLOYEE));

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User registeredUser = authService.register(registerRequest);

        assertNotNull(registeredUser);
        assertEquals("user", registeredUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("newuser@example.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testChangePassword_Success() {
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setCurrentPassword("currentPassword");
        passwordChangeRequest.setNewPassword("NewPassword@123");
        passwordChangeRequest.setConfirmPassword("NewPassword@123");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("currentPassword", mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("encodedNewPassword");

        ApiResponse response = authService.changePassword(passwordChangeRequest);

        assertTrue(response.getSuccess());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() {
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setCurrentPassword("wrongPassword");
        passwordChangeRequest.setNewPassword("NewPassword@123");
        passwordChangeRequest.setConfirmPassword("NewPassword@123");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", mockUser.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.changePassword(passwordChangeRequest));
    }

    @Test
    void testForgotPassword_Success() throws MessagingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        authService.forgotPassword("test@example.com");

        verify(userRepository, times(1)).save(mockUser);
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), eq("Reset Password"), anyString());
    }

    @Test
    void testForgotPassword_EmailNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.forgotPassword("nonexistent@example.com"));
    }

    @Test
    void testResetPassword_Success() {
        mockUser.setResetToken("resetToken");

        when(userRepository.findByResetToken("resetToken")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("encodedNewPassword");

        authService.resetPassword("resetToken", "NewPassword@123");

        assertNull(mockUser.getResetToken());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testResetPassword_InvalidToken() {
        when(userRepository.findByResetToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.resetPassword("invalidToken", "NewPassword@123"));
    }

    @Test
    void testIsAccountLocked_NotLocked() {
        mockUser.setAccountLocked(false);

        boolean isLocked = authService.isAccountLocked(mockUser);

        assertFalse(isLocked);
    }

    @Test
    void testIsAccountLocked_LockedButExpired() {
        mockUser.setAccountLocked(true);
        mockUser.setLockTime(System.currentTimeMillis() - (AuthService.LOCK_TIME_DURATION + 1000));

        boolean isLocked = authService.isAccountLocked(mockUser);

        assertFalse(isLocked);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testIsAccountLocked_StillLocked() {
        mockUser.setAccountLocked(true);
        mockUser.setLockTime(System.currentTimeMillis());

        boolean isLocked = authService.isAccountLocked(mockUser);

        assertTrue(isLocked);
    }
}