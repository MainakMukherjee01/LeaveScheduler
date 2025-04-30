package com.sap.fsad.leaveApp.dto.response;

import java.util.Set;

import com.sap.fsad.leaveApp.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<UserRole> roles;

    public JwtResponse(String token, Long id, String username, String email, Set<UserRole> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}