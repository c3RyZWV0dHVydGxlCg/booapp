package com.pavelmakhov.booapp.domain.service;

import com.pavelmakhov.booapp.domain.model.Appointment;
import com.pavelmakhov.booapp.domain.model.TimeSlot;
import com.pavelmakhov.booapp.domain.repository.AppointmentRepository;
import com.pavelmakhov.booapp.domain.repository.PatientRepository;
import com.pavelmakhov.booapp.domain.repository.ProviderRepository;
import com.pavelmakhov.booapp.domain.repository.TimeSlotRepository;
import com.pavelmakhov.booapp.web.v1.BookingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final TimeSlotRepository timeSlotRepository;
    private final ProviderRepository providerRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository repository, TimeSlotRepository timeSlotRepository, ProviderRepository providerRepository, PatientRepository patientRepository) {
        this.repository = repository;
        this.timeSlotRepository = timeSlotRepository;
        this.providerRepository = providerRepository;
        this.patientRepository = patientRepository;
    }

    public List<Appointment> findProviderAppointments(Long id, LocalDate from, LocalDate to) {
        return repository.findAllByProvider_IdAndDateAfterAndDateBefore(id, from.minusDays(1), to);
    }

    public Map<LocalDate, List<TimeSlot>> findProviderAvailabilities(Long id, LocalDate from, LocalDate to) {
        final List<TimeSlot> allSlots = timeSlotRepository.findAll();

        final Map<LocalDate, List<TimeSlot>> collect = findProviderAppointments(id, from, to)
                .stream()
                .collect(groupingBy(Appointment::getDate, collectingAndThen(
                        toList(),
                        appointments -> {
                            final List<TimeSlot> takenSlots = appointments
                                    .stream()
                                    .map(Appointment::getTimeSlot)
                                    .collect(toList());

                            List<TimeSlot> availableSlots = new ArrayList<>(allSlots);
                            availableSlots.removeAll(takenSlots);
                            return availableSlots;
                        })));

        from.datesUntil(to)
                .forEach(date -> collect.putIfAbsent(date, allSlots));

        return collect;
    }

    public Appointment createAppointment(Long providerId, BookingRequest request) {

        Appointment appointment = new Appointment();
        appointment.setProvider(providerRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Provider with id %s does not exist", providerId))));
        appointment.setTimeSlot(timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timeslot with given id does not exist")));
        appointment.setPatient(patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with given id does not exist")));

        appointment.setDate(request.getDate());

        return repository.save(appointment);
    }
}
