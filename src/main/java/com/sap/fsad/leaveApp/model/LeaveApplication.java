package com.sap.fsad.leaveApp.model;

import com.sap.fsad.leaveApp.model.enums.LeaveStatus;
import com.sap.fsad.leaveApp.model.enums.LeaveType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @NotBlank
    @Size(max = 255)
    private String reason;

    @Size(max = 255)
    private String contactAddress;

    @Size(max = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    public LeaveStatus status = LeaveStatus.PENDING;

    @CreatedDate
    private LocalDateTime appliedOn;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedOn;

    @Size(max = 255)
    private String remarks;

    private Integer numberOfDays;

    @Size(max = 255)
    private String attachmentPath;

    @CreatedDate
    private LocalDateTime createdAt;

    @Size(max = 100)
    @Email
    private String superiorEmail;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
