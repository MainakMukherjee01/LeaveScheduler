package com.sap.fsad.leaveApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        @Size(max = 50)
        private String username;

        @NotBlank
        @Size(max = 100)
        private String password;

        @NotBlank
        @Size(max = 100)
        private String fullName;

        @NotBlank
        @Size(max = 50)
        @Email
        private String email;

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
        @Enumerated(EnumType.STRING)
        private Set<UserRole> roles = new HashSet<>();

        @Size(max = 50)
        private String department;

        @ManyToOne
        @JoinColumn(name = "manager_id")
        private User manager;

        @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
        @JsonIgnore
        private Set<User> subordinates = new HashSet<>();

        private LocalDate joiningDate;

        private int failedLoginAttempts = 0;

        @Size(max = 20)
        private String phone;

        @Size(max = 100)
        private String emergencyContact;

        private boolean isActive = true;

        private boolean isAccountLocked = false;

        private Long lockTime = 0L;

        private LocalDateTime lastLogin;

        @CreatedDate
        private LocalDateTime createdAt;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        @JsonIgnore
        private Set<LeaveBalance> leaveBalances = new HashSet<>();

        @Column(name = "reset_token")
        private String resetToken;
}