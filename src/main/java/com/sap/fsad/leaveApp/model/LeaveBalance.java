package com.sap.fsad.leaveApp.model;

import com.sap.fsad.leaveApp.model.enums.LeaveType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @NotNull
    private Float balance = 0f;

    @NotNull
    private Float used = 0f;

    @Column(name = "leave_year")
    private Integer year;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructor with user and leave type
    public LeaveBalance(User user, LeaveType leaveType, Float initialBalance) {
        this.user = user;
        this.leaveType = leaveType;
        this.balance = initialBalance;
        this.used = 0f;
        this.year = LocalDateTime.now().getYear();
    }
}
