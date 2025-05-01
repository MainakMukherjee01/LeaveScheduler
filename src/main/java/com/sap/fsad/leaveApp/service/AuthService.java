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
import com.sap.fsad.leaveApp.model.BlacklistedToken;
import com.sap.fsad.leaveApp.model.LeaveBalance;
import com.sap.fsad.leaveApp.model.LeavePolicy;
import com.sap.fsad.leaveApp.repository.BlacklistTokenRepository;
import com.sap.fsad.leaveApp.repository.LeaveBalanceRepository;
import com.sap.fsad.leaveApp.repository.LeavePolicyRepository;
import com.sap.fsad.leaveApp.repository.UserRepository;
import com.sap.fsad.leaveApp.security.CustomUserDetails;
import com.sap.fsad.leaveApp.security.JwtTokenProvider;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    static final long LOCK_TIME_DURATION = 5 * 60 * 1000;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository; // Optional: For token blacklisting

    public ApiResponse logout(String token) {
        // Extract the token without the "Bearer " prefix
        String jwt = token.replace("Bearer ", "");

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(jwt);
        blacklistedToken.setExpiryDate(tokenProvider.getExpiryDateFromToken(jwt));
        blacklistTokenRepository.save(blacklistedToken);

        return new ApiResponse(true, "User logged out successfully");
    }

    /**
     * User login
     */
    public JwtResponse login(LoginRequest loginRequest) {

        User userName = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (isAccountLocked(userName)) {
            throw new BadRequestException("Account is locked. Try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            resetFailedAttempts(userName);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Update last login time
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    user.getRoles());
        } catch (Exception e) {
            increaseFailedAttempts(userName);
            throw new BadRequestException("Invalid username or password");
        }
    }

    /**
     * Register a new user
     */
    @Transactional
    public User register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Check if manager exists
        User manager = null;
        if (registerRequest.getManagerId() != null) {
            manager = userRepository.findById(registerRequest.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", registerRequest.getManagerId()));
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setRoles(registerRequest.getRoles());
        user.setDepartment(registerRequest.getDepartment());
        user.setManager(manager);
        user.setJoiningDate(registerRequest.getJoiningDate());
        user.setPhone(registerRequest.getPhone());
        user.setEmergencyContact(registerRequest.getEmergencyContact());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Initialize leave balances based on leave policy
        initializeLeaveBalances(savedUser);

        return savedUser;
    }

    @Transactional
    public List<User> register(List<RegisterRequest> registerRequests) {
        List<User> registeredUsers = new ArrayList<>();

        for (RegisterRequest registerRequest : registerRequests) {
            // Check if username already exists
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                throw new BadRequestException("Username is already taken: " + registerRequest.getUsername());
            }

            // Check if email already exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new BadRequestException("Email is already in use: " + registerRequest.getEmail());
            }

            // Check if manager exists
            User manager = null;
            if (registerRequest.getManagerId() != null) {
                manager = userRepository.findById(registerRequest.getManagerId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", registerRequest.getManagerId()));
            }

            if (!isValidPassword(registerRequest.getPassword())) {
                throw new BadRequestException("Password does not meet the required strength");
            }

            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFullName(registerRequest.getFullName());
            user.setEmail(registerRequest.getEmail());
            user.setRoles(registerRequest.getRoles());
            user.setDepartment(registerRequest.getDepartment());
            user.setManager(manager);
            user.setJoiningDate(registerRequest.getJoiningDate());
            user.setPhone(registerRequest.getPhone());
            user.setEmergencyContact(registerRequest.getEmergencyContact());
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            // Initialize leave balances based on leave policy
            initializeLeaveBalances(savedUser);

            registeredUsers.add(savedUser);
        }

        return registeredUsers;
    }

    /**
     * Initialize leave balances for a new user based on leave policies
     */
    private void initializeLeaveBalances(User user) {
        List<LeavePolicy> policies = new ArrayList<>();
        for (UserRole role : user.getRoles()) {
            policies.addAll(leavePolicyRepository.findByApplicableRolesAndActive(role));
        }
        int currentYear = LocalDateTime.now().getYear();

        for (LeavePolicy policy : policies) {
            LeaveBalance leaveBalance = new LeaveBalance(
                    user,
                    policy.getLeaveType(),
                    policy.getAnnualCredit());
            leaveBalance.setYear(currentYear);
            leaveBalanceRepository.save(leaveBalance);
        }
    }

    public ApiResponse changePassword(PasswordChangeRequest passwordChangeRequest) {
        User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", "current user"));

        // Validate current password
        if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (!isValidPassword(passwordChangeRequest.getNewPassword())) {
            throw new BadRequestException("Password does not meet the required strength");
        }

        // Validate new password and confirm password match
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);

        return new ApiResponse(true, "Password changed successfully");
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + resetToken;
        try {
            emailService.sendEmail(user.getEmail(), "Reset Password",
                    "Click the link to reset your password: " + resetLink);
        } catch (MessagingException e) {
            throw new BadRequestException("Failed to send reset password email");
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("User", "reset token", token));

        if (!isValidPassword(newPassword)) {
            throw new BadRequestException("Password does not meet the required strength");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear the reset token
        userRepository.save(user);
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$");
    }

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailAttempts);
        if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockTime(System.currentTimeMillis());
        }
        userRepository.save(user);
    }

    public void resetFailedAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }

    public boolean isAccountLocked(User user) {
        if (!user.isAccountLocked())
            return false;

        long lockTimeElapsed = System.currentTimeMillis() - user.getLockTime();
        if (lockTimeElapsed > LOCK_TIME_DURATION) {
            resetFailedAttempts(user);
            return false;
        }
        return true;
    }
}
