package com.sap.fsad.leaveApp.dto.request;

import com.sap.fsad.leaveApp.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    private Set<UserRole> roles;

    @Size(max = 50)
    private String department;

    private Long managerId;

    private LocalDate joiningDate;

    @Size(max = 20)
    private String phone;

    @Size(max = 100)
    private String emergencyContact;
}