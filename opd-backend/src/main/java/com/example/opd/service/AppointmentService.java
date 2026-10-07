package com.example.opd.service;

import com.example.opd.dto.request.AppointmentRequest;
import com.example.opd.dto.response.AppointmentResponse;
import com.example.opd.entity.Appointment;
import com.example.opd.entity.AppointmentStatus;
import com.example.opd.entity.Doctor;
import com.example.opd.entity.Patient;
import com.example.opd.exception.ConflictException;
import com.example.opd.exception.ResourceNotFoundException;
import com.example.opd.repository.AppointmentRepository;
import com.example.opd.repository.DoctorRepository;
import com.example.opd.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + request.getDoctorId()));

        if (request.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past");
        }

        boolean doubleBooked = appointmentRepository.existsByDoctorIdAndAppointmentTimeAndStatusNot(
                doctor.getId(),
                request.getAppointmentTime(),
                AppointmentStatus.CANCELLED
        );

        if (doubleBooked) {
            throw new ConflictException("Doctor already has an appointment at this time");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getTodaysAppointments() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        return appointmentRepository.findByAppointmentTimeBetweenOrderByAppointmentTimeAsc(startOfDay, endOfDay)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        return mapToResponse(appointment);
    }

    public AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .patientPhone(appointment.getPatient().getPhone())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
