package com.example.opd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "blood_pressure", nullable = false, length = 10)
    private String bloodPressure;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(nullable = false, length = 500)
    private String notes;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
}
