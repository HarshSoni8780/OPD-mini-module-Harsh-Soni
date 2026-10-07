package com.example.opd.repository;

import com.example.opd.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);

    boolean existsByPhone(String phone);

    List<Patient> findAllByOrderByCreatedAtDesc();
}
