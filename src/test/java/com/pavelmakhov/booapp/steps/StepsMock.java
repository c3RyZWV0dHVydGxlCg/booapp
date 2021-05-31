//package com.pavelmakhov.booapp.steps;
//
//import com.pavelmakhov.booapp.CucumberSpringContextConfigurationTest;
//import com.pavelmakhov.booapp.domain.model.Appointment;
//import com.pavelmakhov.booapp.domain.model.Clinic;
//import com.pavelmakhov.booapp.domain.model.Patient;
//import com.pavelmakhov.booapp.domain.model.Provider;
//import com.pavelmakhov.booapp.domain.model.TimeSlot;
//import com.pavelmakhov.booapp.domain.repository.AppointmentRepository;
//import com.pavelmakhov.booapp.domain.repository.ClinicRepository;
//import com.pavelmakhov.booapp.domain.repository.PatientRepository;
//import com.pavelmakhov.booapp.domain.repository.ProviderRepository;
//import com.pavelmakhov.booapp.domain.repository.TimeSlotRepository;
//import io.cucumber.datatable.DataTable;
//import io.cucumber.java8.En;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//import java.util.Objects;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.hamcrest.Matchers.containsInAnyOrder;
//import static org.hamcrest.Matchers.containsString;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//public class StepsMock extends CucumberSpringContextConfigurationTest implements En {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private ProviderRepository providerRepository;
//    @Mock
//    private ClinicRepository clinicRepository;
//    @Mock
//    private TimeSlotRepository timeSlotRepository;
//    @Mock
//    private PatientRepository patientRepository;
//    @Mock
//    private AppointmentRepository appointmentRepository;
//
//    private ResultActions perform;
//    private Clinic clinic;
//
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
//
//    public StepsMock() {
//        Before(() -> {
//
//            perform = null;
//            clinic = new Clinic();
//        });
//
//
//
//        Given("we are in {string}", (String clinicName) -> {
//            clinic.setName(clinicName);
//            clinicRepository.save(clinic);
//        });
//
//        Given("no providers exist", () -> providerRepository.deleteAll());
//
//        Given("following providers exist", (DataTable providersTable) ->
//                providersTable.asMaps()
//                        .forEach(row -> {
//                            Provider toSave = new Provider();
//                            toSave.setId(Long.valueOf(row.get("id")));
//                            toSave.setClinic(clinic);
//                            toSave.setFirstName(row.get("firstname"));
//                            toSave.setLastName(row.get("lastname"));
//                            toSave.setOccupation(row.get("occupation"));
//                            providerRepository.save(toSave);
//                        }));
//
//        Given("following patients exist", (DataTable patientsTable) ->
//                patientsTable.asMaps()
//                        .forEach(row -> {
//                            Patient toSave = new Patient();
//                            toSave.setId(Long.valueOf(row.get("id")));
//                            toSave.setFirstName(row.get("firstname"));
//                            toSave.setLastName(row.get("lastname"));
//                            patientRepository.save(toSave);
//                        }));
//
//        Given("^following timeslots exist:$", (DataTable timeslotTable) -> {
//            timeslotTable.asMaps()
//                    .forEach(row -> {
//                        TimeSlot toSave = new TimeSlot();
//                        toSave.setId(Long.valueOf(row.get("id")));
//                        toSave.setStart(LocalTime.parse(row.get("start"), formatter));
//                        toSave.setEnd(LocalTime.parse(row.get("end"), formatter));
//                        timeSlotRepository.save(toSave);
//                    });
//        });
//
//        Given("no appointments exist", () -> {
//            appointmentRepository.deleteAll();
//        });
//
//        Given("^following appointment exist:$", (DataTable appointmentTable) -> {
//            appointmentTable.asMaps()
//                    .forEach(row -> {
//                        Appointment toSave = new Appointment();
//                        toSave.setId(Long.valueOf(row.get("id")));
//                        toSave.setDate(LocalDate.parse(row.get("date")));
//                        Provider provider = new Provider();
//                        provider.setId(Long.valueOf(row.get("provider")));
//                        toSave.setProvider(provider);
//                        Patient patient = new Patient();
//                        patient.setId(Long.valueOf(row.get("patient")));
//                        toSave.setPatient(patient);
//                        TimeSlot timeSlot = new TimeSlot();
//                        timeSlot.setId(Long.valueOf(row.get("timeSlot")));
//                        toSave.setTimeSlot(timeSlot);
//
//                        appointmentRepository.save(toSave);
//                    });
//        });
//
//
//        When("I call {word} method for {word}", (String method, String path) ->
//                perform = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.valueOf(method), path))
//                        .andDo(MockMvcResultHandlers.print()));
//
//        When("I call {word} method for {word} with body", (String method, String path, String body) -> {
//            perform = mockMvc.perform(MockMvcRequestBuilders
//                    .request(HttpMethod.valueOf(method), path)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body))
//                    .andDo(MockMvcResultHandlers.print());
//        });
//
//
//        Then("the status code should be {int}", (Integer statusCode) ->
//                perform.andExpect(status().is(statusCode)));
//        Then("the status message should contain {string}", (String statusMessage) ->
//                perform.andExpect(status().reason(containsString(statusMessage))));
//
//        Then("the response should be", (String json) ->
//                perform.andExpect(content().json(json)));
//
//        Then("the response should contain providers with last names", (String names) ->
//                perform.andExpect(jsonPath("$[*].lastName", containsInAnyOrder(names.split(",")))));
//
//        Then("^following appointment should exist:$", (DataTable appointmentTable) -> {
//            final Map<String, String> expectedAppointmentRow = appointmentTable.asMaps().get(0);
//            assertThat(
//                    appointmentRepository.findAll()
//                            .stream()
//                            .filter(appointment ->
//                                    appointment.getDate().isEqual(LocalDate.parse(expectedAppointmentRow.get("date")))
//                                            && Objects.equals(appointment.getTimeSlot().getId(), Long.valueOf(expectedAppointmentRow.get("timeSlot")))
//                                            && Objects.equals(appointment.getPatient().getId(), Long.valueOf(expectedAppointmentRow.get("patient")))
//                                            && Objects.equals(appointment.getProvider().getId(), Long.valueOf(expectedAppointmentRow.get("provider")))
//                            )
//                            .findAny()).isPresent();
//        });
//    }
//}
