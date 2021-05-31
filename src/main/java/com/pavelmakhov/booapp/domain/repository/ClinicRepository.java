package com.pavelmakhov.booapp.domain.repository;

import com.pavelmakhov.booapp.domain.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository  extends JpaRepository<Clinic, Long> {
}
