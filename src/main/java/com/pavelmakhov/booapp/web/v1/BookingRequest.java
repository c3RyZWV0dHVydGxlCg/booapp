package com.pavelmakhov.booapp.web.v1;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private LocalDate date;
    private Long patientId;
    private Long timeSlotId;
}
