package com.sap.fsad.leaveApp.dto.request;

import java.util.Set;

import com.sap.fsad.leaveApp.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @Size(max = 100)
    private String fullName;

    @Email
    @Size(max = 50)
    private String email;

    @Size(max = 50)
    private String department;

    private Set<UserRole> roles;

    private Long managerId;

    @Size(min = 6, max = 100)
    private String password;

    private Boolean isActive;
}