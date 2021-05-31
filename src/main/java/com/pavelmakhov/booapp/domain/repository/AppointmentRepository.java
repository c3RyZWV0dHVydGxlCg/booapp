package com.pavelmakhov.booapp.domain.repository;

import com.pavelmakhov.booapp.domain.model.Appointment;
import com.pavelmakhov.booapp.domain.model.Clinic;
import com.pavelmakhov.booapp.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByProvider_IdAndDateAfterAndDateBefore(Long provider_id, LocalDate date, LocalDate date2);
}
