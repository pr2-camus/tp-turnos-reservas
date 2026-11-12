package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.prog2.camus.turnos.IntegrationTest;
import ar.edu.um.prog2.camus.turnos.domain.User;
import ar.edu.um.prog2.camus.turnos.repository.UserRepository;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.RecoverPendingReservationsUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CatedraReservationsClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import ar.edu.um.prog2.camus.turnos.web.rest.vm.LoginVM;
import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Seguridad y recuperacion de reservas de punta a punta: dos usuarios reales con su JWT,
 * la base real y la catedra simulada.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Transactional
class ReservationControllerSecurityIT {

    private static final Instant PASADO = Instant.parse("2020-01-01T00:00:00Z");
    private static final Instant FUTURO = Instant.parse("2099-01-01T00:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReservationProcessRepository processRepository;

    @Autowired
    private RecoverPendingReservationsUseCase recoverUseCase;

    @MockitoBean
    private HoldClient holdClient;

    @MockitoBean
    private CatedraReservationsClient catedraClient;

    private User createUser(String login) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(login + "@example.com");
        user.setFirstName("Nombre");
        user.setLastName("Apellido");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));
        return userRepository.saveAndFlush(user);
    }

    private String tokenOf(String login) throws Exception {
        LoginVM vm = new LoginVM();
        vm.setUsername(login);
        vm.setPassword("test");
        String body = mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return JsonPath.read(body, "$.id_token");
    }

    private ReservationProcess saveProcess(User owner, String processId, ReservationStatus status, Instant expiresAt) {
        return processRepository.save(
            ReservationProcess.restore(
                null,
                owner.getId(),
                "hold-" + processId,
                processId,
                null,
                201L,
                LocalDate.of(2026, 11, 16),
                LocalTime.of(12, 0),
                expiresAt,
                status
            )
        );
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    // ---- Acceso cruzado entre dos usuarios ----

    @Test
    void otroUsuarioNoVeLaReservaAjena() throws Exception {
        User a = createUser("usuario-a-it");
        createUser("usuario-b-it");
        saveProcess(a, "process-a1", ReservationStatus.CONFIRMED, FUTURO);
        String tokenB = tokenOf("usuario-b-it");

        mockMvc.perform(get("/api/reservations/process-a1").header("Authorization", bearer(tokenB))).andExpect(status().isNotFound());
    }

    @Test
    void otroUsuarioNoCancelaLaReservaAjena() throws Exception {
        User a = createUser("usuario-a-it");
        createUser("usuario-b-it");
        saveProcess(a, "process-a1", ReservationStatus.CONFIRMED, FUTURO);
        String tokenA = tokenOf("usuario-a-it");
        String tokenB = tokenOf("usuario-b-it");

        mockMvc
            .perform(post("/api/reservations/process-a1/cancel").header("Authorization", bearer(tokenB)))
            .andExpect(status().isNotFound());

        verify(holdClient, never()).cancelReservation(any(), any());
        mockMvc
            .perform(get("/api/reservations/process-a1").header("Authorization", bearer(tokenA)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void cadaUsuarioVeSoloSusReservasEnElListado() throws Exception {
        User a = createUser("usuario-a-it");
        createUser("usuario-b-it");
        saveProcess(a, "process-a1", ReservationStatus.CONFIRMED, FUTURO);
        String tokenA = tokenOf("usuario-a-it");
        String tokenB = tokenOf("usuario-b-it");

        mockMvc
            .perform(get("/api/reservations").header("Authorization", bearer(tokenA)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].reservationProcessId").value("process-a1"));
        mockMvc
            .perform(get("/api/reservations").header("Authorization", bearer(tokenB)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void sinTokenSeRechazaElAcceso() throws Exception {
        mockMvc.perform(get("/api/reservations")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reservations/process-a1")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/reservations/process-a1/cancel")).andExpect(status().isUnauthorized());
    }

    // ---- Cancelacion repetida ----

    @Test
    void cancelarDosVecesEsIdempotenteYSoloLlamaUnaVezALaCatedra() throws Exception {
        User a = createUser("usuario-a-it");
        saveProcess(a, "process-a1", ReservationStatus.CONFIRMED, FUTURO);
        String tokenA = tokenOf("usuario-a-it");

        for (int i = 0; i < 2; i++) {
            mockMvc
                .perform(post("/api/reservations/process-a1/cancel").header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        }

        verify(holdClient, times(1)).cancelReservation(any(), any());
    }

    // ---- Reconciliacion de procesos vencidos ----

    @Test
    void laRecuperacionVenceUnProcesoPendienteQueYaPasoSuPlazo() throws Exception {
        User a = createUser("usuario-a-it");
        saveProcess(a, "process-a1", ReservationStatus.WAITING_FOR_PHONE, PASADO);
        when(catedraClient.listReservations()).thenReturn(List.of());
        String tokenA = tokenOf("usuario-a-it");

        recoverUseCase.recover();

        mockMvc
            .perform(get("/api/reservations/process-a1").header("Authorization", bearer(tokenA)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    @Test
    void laRecuperacionConfirmaUnTelefonoEnviadoQueLaCatedraYaConfirmo() throws Exception {
        User a = createUser("usuario-a-it");
        saveProcess(a, "process-a1", ReservationStatus.PHONE_SUBMITTED, PASADO);
        when(catedraClient.listReservations()).thenReturn(
            List.of(new CatedraReservation("process-a1", CatedraReservationStatus.CONFIRMED))
        );
        String tokenA = tokenOf("usuario-a-it");

        recoverUseCase.recover();

        mockMvc
            .perform(get("/api/reservations/process-a1").header("Authorization", bearer(tokenA)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
