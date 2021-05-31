package com.pavelmakhov.booapp.web.v1.controller;

import com.pavelmakhov.booapp.domain.model.Appointment;
import com.pavelmakhov.booapp.domain.model.Provider;
import com.pavelmakhov.booapp.domain.model.TimeSlot;
import com.pavelmakhov.booapp.domain.service.AppointmentService;
import com.pavelmakhov.booapp.domain.service.ProviderService;
import com.pavelmakhov.booapp.web.v1.BookingRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class ProviderController {

    private final ProviderService service;
    private final AppointmentService appointmentService;

    public ProviderController(ProviderService service, AppointmentService appointmentService) {
        this.service = service;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/providers")
    public List<Provider> findBySearchTerm(@RequestParam Optional<String> occupation) {

        return occupation
                .map(service::findByOccupation)
                .orElseGet(service::findAll);
    }

    @GetMapping("/providers/{providerId}/availabilities")
    public Map<LocalDate, List<TimeSlot>> findAvailabilities(@PathVariable Long providerId,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from date is after to date");
        }

        return appointmentService.findProviderAvailabilities(providerId, from, to);
    }

    @PostMapping("/providers/{providerId}/appointments")
    public Appointment bookAppointment(@PathVariable Long providerId, @RequestBody BookingRequest request) {
        return appointmentService.createAppointment(providerId, request);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Constraint violation")
    public void conflict() {
    }
}
