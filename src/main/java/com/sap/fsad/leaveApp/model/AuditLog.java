package com.sap.fsad.leaveApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "audit_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;

    @ManyToOne
    @JoinColumn(name = "leave_application_id", nullable = true)
    @JsonIgnore
    private LeaveApplication leaveApplication;

    private String action;

    private String details;

    private LocalDateTime actionTimestamp;
}