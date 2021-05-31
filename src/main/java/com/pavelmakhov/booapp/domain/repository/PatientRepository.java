package com.pavelmakhov.booapp.domain.repository;

import com.pavelmakhov.booapp.domain.model.Patient;
import com.pavelmakhov.booapp.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
