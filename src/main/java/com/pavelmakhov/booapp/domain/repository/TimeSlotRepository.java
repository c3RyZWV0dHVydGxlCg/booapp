package com.pavelmakhov.booapp.domain.repository;

import com.pavelmakhov.booapp.domain.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

}
