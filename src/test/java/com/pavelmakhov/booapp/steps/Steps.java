package com.pavelmakhov.booapp.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pavelmakhov.booapp.CucumberSpringContextConfigurationTest;
import com.pavelmakhov.booapp.domain.model.Appointment;
import com.pavelmakhov.booapp.domain.model.Clinic;
import com.pavelmakhov.booapp.domain.model.Patient;
import com.pavelmakhov.booapp.domain.model.Provider;
import com.pavelmakhov.booapp.domain.model.TimeSlot;
import com.pavelmakhov.booapp.domain.repository.AppointmentRepository;
import com.pavelmakhov.booapp.domain.repository.ClinicRepository;
import com.pavelmakhov.booapp.domain.repository.PatientRepository;
import com.pavelmakhov.booapp.domain.repository.ProviderRepository;
import com.pavelmakhov.booapp.domain.repository.TimeSlotRepository;
import io.cucumber.datatable.DataTable;

import io.cucumber.java8.En;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class Steps extends CucumberSpringContextConfigurationTest implements En {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    private ResultActions perform;
    private Clinic clinic;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

    public Steps() {
        Before(() -> {

            perform = null;
            clinic = new Clinic();
        });

        After(() -> {
            appointmentRepository.deleteAll();
            providerRepository.deleteAll();
            patientRepository.deleteAll();
            timeSlotRepository.deleteAll();
        });


        Given("we are in {string}", (String clinicName) -> {
            clinic.setName(clinicName);
            clinicRepository.save(clinic);
        });

        Given("no providers exist", () -> providerRepository.deleteAll());

        Given("following provider(s) exist:", (DataTable providersTable) ->
                providersTable.asMaps()
                        .forEach(row -> {
                            if (!providerRepository.existsByFirstNameAndLastName(row.get("firstname"), row.get("lastname"))) {
                                if (row.get("id") != null) {
                                    providerRepository.insertProviderForceId(Long.parseLong(row.get("id")), row.get("firstname"), row.get("lastname"), row.get("occupation"), clinic.getId());
                                } else {
                                    Provider toSave = new Provider();
                                    toSave.setClinic(clinic);
                                    toSave.setFirstName(row.get("firstname"));
                                    toSave.setLastName(row.get("lastname"));
                                    toSave.setOccupation(row.get("occupation"));
                                    providerRepository.save(toSave);
                                }
                            }
                        }));

        Given("following patients exist", (DataTable patientsTable) ->
                patientsTable.asMaps()
                        .forEach(row -> {
                            Patient toSave = new Patient();
                            if (row.get("id") != null) {
                                toSave.setId(Long.valueOf(row.get("id")));
                            }
                            toSave.setFirstName(row.get("firstname"));
                            toSave.setLastName(row.get("lastname"));
                            patientRepository.save(toSave);
                        }));

        Given("^following timeslots exist:$", (DataTable timeslotTable) -> {
            timeslotTable.asMaps()
                    .forEach(row -> {
                        TimeSlot toSave = new TimeSlot();
//                        toSave.setId(Long.valueOf(row.get("id")));
                        toSave.setStart(LocalTime.parse(row.get("start"), formatter));
                        toSave.setEnd(LocalTime.parse(row.get("end"), formatter));
                        timeSlotRepository.save(toSave);
                    });
        });

        Given("no appointments exist", () -> {
            appointmentRepository.deleteAll();
        });

        Given("following appointment(s) exist:", (DataTable appointmentTable) -> {
            final List<Provider> allProviders = providerRepository.findAll();
            final List<Patient> allPatients = patientRepository.findAll();
            final List<TimeSlot> allTimeSlots = timeSlotRepository.findAll();
            appointmentTable.asMaps()
                    .forEach(row -> {

                        Appointment toSave = new Appointment();
                        toSave.setProvider(allProviders
                                .stream()
                                .filter(p -> p.getLastName().equals(row.get("provider")))
                                .findFirst()
                                .get());

                        toSave.setPatient(allPatients
                                .stream()
                                .filter(p -> p.getLastName().equals(row.get("patient")))
                                .findAny()
                                .get());

                        toSave.setTimeSlot(allTimeSlots
                                .stream()
                                .filter(t -> t.getStart().equals(LocalTime.parse(row.get("timeSlot").split("-")[0], formatter)))
                                .findAny()
                                .get());

                        toSave.setDate(LocalDate.parse(row.get("date")));
                        appointmentRepository.save(toSave);
                    });
        });


        When("I call {word} method for {word}", (String method, String path) ->
                perform = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.valueOf(method), path))
                        .andDo(MockMvcResultHandlers.print()));

        When("I call {word} method for {word} with body", (String method, String path, String body) ->
                perform = mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.valueOf(method), path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andDo(MockMvcResultHandlers.print()));

        When("I call {word} method for {word} to create a following appointment", (String method, String path, DataTable params) -> {

            final Map<String, String> map = params.asMaps().get(0);
            Map<String, String> m = new HashMap<>();
            m.put("date", map.get("date"));
            m.put("patientId", String.valueOf(patientRepository.findAll()
                    .stream()
                    .filter(p -> p.getLastName().equals(map.get("patient")))
                    .map(Patient::getId)
                    .findAny()
                    .orElse(-1L)));

            m.put("timeSlotId", String.valueOf(timeSlotRepository.findAll()
                    .stream()
                    .filter(t -> t.getStart().equals(LocalTime.parse(map.get("timeSlot").split("-")[0], formatter)))
                    .map(TimeSlot::getId)
                    .findAny()
                    .orElse(-1L)));

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            perform = mockMvc.perform(MockMvcRequestBuilders
                    .request(HttpMethod.valueOf(method), path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(m)))
                    .andDo(MockMvcResultHandlers.print());
        });


        Then("the status code should be {int}", (Integer statusCode) ->
                perform.andExpect(status().is(statusCode)));
        Then("the status message should contain {string}", (String statusMessage) ->
                perform.andExpect(status().reason(containsString(statusMessage))));

        Then("the response should be", (String json) ->
                perform.andExpect(content().json(json)));

        Then("the response should contain providers with last names", (String names) -> {
            if (names.isEmpty()) {
                perform.andExpect(content().string("[]"));
            } else {
                perform.andExpect(jsonPath("$[*].lastName", containsInAnyOrder(names.split(","))));
            }
        });

        Then("the response should have following time slots:", (DataTable timeSlotTable) -> {
            final String contentAsString = perform.andReturn().getResponse().getContentAsString();
            TypeReference<Map<String, List<TimeSlot>>> typeRef = new TypeReference<>() {
            };
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            final Map<String, List<TimeSlot>> localDateListMap = mapper.readValue(contentAsString, typeRef);

            timeSlotTable.asMaps()
                    .forEach(row -> {
                        List<TimeSlot> timeSlots = localDateListMap.get(row.get("date"));
                        assertThat(timeSlots)
                                .extracting(timeSlot -> timeSlot.getStart().toString(), timeSlot1 -> timeSlot1.getEnd().toString())
                                .contains(tuple(row.get("start"), row.get("end")));
                    });
        });

        Then("^following appointment should exist:$", (DataTable appointmentTable) -> {
            final Map<String, String> expectedAppointmentRow = appointmentTable.asMaps().get(0);
            assertThat(
                    appointmentRepository.findAll()
                            .stream()
                            .filter(appointment ->
                                    appointment.getDate().isEqual(LocalDate.parse(expectedAppointmentRow.get("date")))
                                            && Objects.equals(appointment.getTimeSlot().getStart(), LocalTime.parse(expectedAppointmentRow.get("timeSlot").split("-")[0], formatter))
                                            && Objects.equals(appointment.getPatient().getLastName(), expectedAppointmentRow.get("patient"))
                                            && Objects.equals(appointment.getProvider().getLastName(), expectedAppointmentRow.get("provider"))
                            )
                            .findAny()).isPresent();
        });
    }
}
