package com.example.opd.repository;

import com.example.opd.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByAppointmentPatientIdOrderByCompletedAtDesc(Long patientId);

    Optional<Consultation> findByAppointmentId(Long appointmentId);
}

