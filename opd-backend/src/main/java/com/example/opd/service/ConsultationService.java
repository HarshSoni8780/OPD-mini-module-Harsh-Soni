package com.example.opd.service;

import com.example.opd.dto.request.ConsultationRequest;
import com.example.opd.dto.response.ConsultationResponse;
import com.example.opd.entity.Appointment;
import com.example.opd.entity.AppointmentStatus;
import com.example.opd.entity.Consultation;
import com.example.opd.exception.ConflictException;
import com.example.opd.exception.ResourceNotFoundException;
import com.example.opd.repository.AppointmentRepository;
import com.example.opd.repository.ConsultationRepository;
import com.example.opd.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public ConsultationResponse completeConsultation(Long appointmentId, ConsultationRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ConflictException("Appointment has already been completed");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ConflictException("Cannot complete a cancelled appointment");
        }

        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .bloodPressure(request.getBloodPressure().trim())
                .temperature(request.getTemperature())
                .notes(request.getNotes().trim())
                .completedAt(LocalDateTime.now())
                .build();

        Consultation savedConsultation = consultationRepository.save(consultation);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return mapToResponse(savedConsultation);
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> getPatientConsultations(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }

        return consultationRepository.findByAppointmentPatientIdOrderByCompletedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ConsultationResponse mapToResponse(Consultation consultation) {
        Appointment appointment = consultation.getAppointment();
        return ConsultationResponse.builder()
                .id(consultation.getId())
                .appointmentId(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .appointmentTime(appointment.getAppointmentTime())
                .bloodPressure(consultation.getBloodPressure())
                .temperature(consultation.getTemperature())
                .notes(consultation.getNotes())
                .completedAt(consultation.getCompletedAt())
                .build();
    }
}
