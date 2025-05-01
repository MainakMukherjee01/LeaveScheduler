package com.sap.fsad.leaveApp.model;

import com.sap.fsad.leaveApp.model.enums.LeaveType;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "leave_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeavePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    private Float annualCredit;

    private Float maxAccumulation;

    private Boolean isCarryForward = false;

    private Integer minDuration = 1;

    private Integer maxDuration;

    private Integer noticeRequired = 1; // Days

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "leave_policy_applicable_roles", joinColumns = @JoinColumn(name = "policy_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> applicableRoles = new HashSet<>();

    private Boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}