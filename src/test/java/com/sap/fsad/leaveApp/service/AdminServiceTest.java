package com.sap.fsad.leaveApp.service;

import com.sap.fsad.leaveApp.dto.request.LeavePolicyRequest;
import com.sap.fsad.leaveApp.dto.request.UserUpdateRequest;
import com.sap.fsad.leaveApp.dto.response.ApiResponse;
import com.sap.fsad.leaveApp.dto.response.DashboardStatsResponse;
import com.sap.fsad.leaveApp.dto.response.UserResponse;
import com.sap.fsad.leaveApp.exception.BadRequestException;
import com.sap.fsad.leaveApp.model.LeaveApplication;
import com.sap.fsad.leaveApp.model.LeavePolicy;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import com.sap.fsad.leaveApp.repository.AuditLogRepository;
import com.sap.fsad.leaveApp.repository.LeaveApplicationRepository;
import com.sap.fsad.leaveApp.repository.LeavePolicyRepository;
import com.sap.fsad.leaveApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeavePolicyRepository leavePolicyRepository;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogRepository auditLogRepository;

    private User adminUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRoles(Set.of(UserRole.ADMIN));
    }

    @Test
    void testGetDashboardStats_Success() {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        when(leaveApplicationRepository.countByUserIdAndStatus(null, LeaveStatus.PENDING)).thenReturn(5);
        when(userRepository.findByRole(any())).thenReturn(Collections.emptyList());
        when(leaveApplicationRepository.findAll()).thenReturn(Collections.emptyList());

        DashboardStatsResponse stats = adminService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalUsers());
        assertEquals(0, stats.getActiveUsers());
        assertEquals(5L, stats.getPendingLeaves());
    }

    @Test
    void testGetDashboardStats_Unauthorized() {
        User nonAdminUser = new User();
        nonAdminUser.setRoles(Set.of(UserRole.EMPLOYEE));
        when(userService.getCurrentUser()).thenReturn(nonAdminUser);

        assertThrows(BadRequestException.class, () -> adminService.getDashboardStats());
    }

    @Test
    void testGetAllUsers_Success() {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponse> users = adminService.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void testGetAllUsers_Unauthorized() {
        User nonAdminUser = new User();
        nonAdminUser.setRoles(Set.of(UserRole.EMPLOYEE));
        when(userService.getCurrentUser()).thenReturn(nonAdminUser);

        assertThrows(BadRequestException.class, () -> adminService.getAllUsers());
    }

    @Test
    void testUpdateUser_Success() {
        User userToUpdate = new User();
        userToUpdate.setId(2L);
        userToUpdate.setEmail("old@example.com");

        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("new@example.com");

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        UserResponse response = adminService.updateUser(2L, request);

        assertNotNull(response);
        assertEquals("new@example.com", userToUpdate.getEmail());
    }

    @Test
    void testUpdateUser_EmailAlreadyInUse() {
        User userToUpdate = new User();
        userToUpdate.setId(2L);
        userToUpdate.setEmail("old@example.com");

        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("new@example.com");

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> adminService.updateUser(2L, request));
    }

    @Test
    void testCreateOrUpdateLeavePolicy_CreateSuccess() {
        LeavePolicyRequest request = new LeavePolicyRequest();
        request.setLeaveType(LeaveType.CASUAL);
        request.setAnnualCredit(15F);

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(leavePolicyRepository.save(any(LeavePolicy.class))).thenReturn(new LeavePolicy());

        LeavePolicy policy = adminService.createOrUpdateLeavePolicy(request);

        assertNotNull(policy);
        verify(leavePolicyRepository, times(1)).save(any(LeavePolicy.class));
    }

    @Test
    void testCreateOrUpdateLeavePolicy_UpdateSuccess() {
        LeavePolicy existingPolicy = new LeavePolicy();
        existingPolicy.setId(1L);

        LeavePolicyRequest request = new LeavePolicyRequest();
        request.setId(1L);
        request.setLeaveType(LeaveType.CASUAL);
        request.setAnnualCredit(15F);

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(leavePolicyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));
        when(leavePolicyRepository.save(any(LeavePolicy.class))).thenReturn(existingPolicy);

        LeavePolicy policy = adminService.createOrUpdateLeavePolicy(request);

        assertNotNull(policy);
        assertEquals(15, policy.getAnnualCredit());
    }

    @Test
    void testDeleteLeavePolicy_Success() {
        LeavePolicy policy = new LeavePolicy();
        policy.setId(1L);

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(leavePolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(leaveApplicationRepository.findAll()).thenReturn(Collections.emptyList());

        ApiResponse response = adminService.deleteLeavePolicy(1L);

        assertTrue(response.getSuccess());
        verify(leavePolicyRepository, times(1)).delete(policy);
    }

    @Test
    void testDeleteLeavePolicy_MarkInactive() {
        LeavePolicy policy = new LeavePolicy();
        policy.setId(1L);

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(leavePolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(leaveApplicationRepository.findAll()).thenReturn(List.of(new LeaveApplication()));

        ApiResponse response = adminService.deleteLeavePolicy(1L);

        assertTrue(response.getSuccess());
        assertFalse(policy.getIsActive());
        verify(leavePolicyRepository, times(1)).save(policy);
    }
}