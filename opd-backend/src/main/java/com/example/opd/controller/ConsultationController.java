package com.example.opd.controller;

import com.example.opd.dto.request.ConsultationRequest;
import com.example.opd.dto.response.ConsultationResponse;
import com.example.opd.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping("/api/appointments/{id}/consultation")
    public ResponseEntity<ConsultationResponse> completeConsultation(
            @PathVariable Long id,
            @Valid @RequestBody ConsultationRequest request) {
        ConsultationResponse response = consultationService.completeConsultation(id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/patients/{id}/consultations")
    public ResponseEntity<List<ConsultationResponse>> getPatientConsultations(@PathVariable Long id) {
        List<ConsultationResponse> consultations = consultationService.getPatientConsultations(id);
        return ResponseEntity.ok(consultations);
    }
}
