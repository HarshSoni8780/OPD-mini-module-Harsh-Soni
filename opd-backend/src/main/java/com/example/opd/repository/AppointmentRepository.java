package com.example.opd.repository;

import com.example.opd.entity.Appointment;
import com.example.opd.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentTimeAndStatusNot(Long doctorId, LocalDateTime appointmentTime, AppointmentStatus status);

    List<Appointment> findByAppointmentTimeBetweenOrderByAppointmentTimeAsc(LocalDateTime start, LocalDateTime end);
}
