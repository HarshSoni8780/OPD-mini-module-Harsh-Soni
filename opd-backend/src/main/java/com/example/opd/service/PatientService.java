package com.example.opd.service;

import com.example.opd.dto.request.PatientRequest;
import com.example.opd.dto.response.PatientResponse;
import com.example.opd.entity.Patient;
import com.example.opd.exception.ConflictException;
import com.example.opd.exception.ResourceNotFoundException;
import com.example.opd.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse registerPatient(PatientRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number already registered: " + request.getPhone());
        }

        Patient patient = Patient.builder()
                .name(request.getName().trim())
                .gender(request.getGender())
                .age(request.getAge())
                .phone(request.getPhone().trim())
                .build();

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients(String search) {
        List<Patient> patients;
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim();
            patients = patientRepository.findByNameContainingIgnoreCaseOrPhoneContaining(query, query);
        } else {
            patients = patientRepository.findAllByOrderByCreatedAtDesc();
        }
        return patients.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return mapToResponse(patient);
    }

    public PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .gender(patient.getGender())
                .age(patient.getAge())
                .phone(patient.getPhone())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
