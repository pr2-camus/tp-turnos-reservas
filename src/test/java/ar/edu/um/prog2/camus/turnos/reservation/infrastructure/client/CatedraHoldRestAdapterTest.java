package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldConfirmation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldCreated;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CatedraHoldRestAdapterTest {

    private MockRestServiceServer server;
    private CatedraHoldRestAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://catedra.test");
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new CatedraHoldRestAdapter(builder.build());
    }

    @Test
    void creaElHoldYMapeaLaRespuesta() {
        server
            .expect(requestTo(containsString("/api/appointment-holds")))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().string(containsString("user-42")))
            .andRespond(
                withStatus(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                        "{\"holdId\":\"hold-1\",\"reservationProcessId\":\"process-1\",\"status\":\"HELD\",\"expiresAt\":\"2026-11-09T15:00:00Z\"}"
                    )
            );

        HoldCreated hold = adapter.createHold(201L, LocalDate.of(2026, 11, 9), LocalTime.of(12, 30), "user-42");

        assertThat(hold.holdId()).isEqualTo("hold-1");
        assertThat(hold.reservationProcessId()).isEqualTo("process-1");
        assertThat(hold.expiresAt()).isNotNull();
        server.verify();
    }

    @Test
    void traduceUnTurnoYaRetenidoAConflicto() {
        server
            .expect(requestTo(containsString("/api/appointment-holds")))
            .andRespond(
                withStatus(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body("{\"status\":409,\"detail\":\"Slot already held\",\"code\":\"SLOT_ALREADY_HELD\"}")
            );

        assertThatThrownBy(() ->
            adapter.createHold(201L, LocalDate.of(2026, 11, 9), LocalTime.of(12, 30), "user-42")
        ).isInstanceOfSatisfying(BusinessException.class, e -> {
            assertThat(e.getCode()).isEqualTo("SLOT_ALREADY_HELD");
            assertThat(e.getKind()).isEqualTo(ErrorKind.CONFLICT);
        });
    }

    @Test
    void confirmaElHoldYMapeaLaRespuesta() {
        server
            .expect(requestTo(containsString("/api/appointment-holds/hold-1/confirm")))
            .andExpect(method(HttpMethod.POST))
            .andRespond(
                withStatus(HttpStatus.ACCEPTED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                        "{\"reservationProcessId\":\"process-1\",\"status\":\"WAITING_FOR_PHONE\",\"expiresAt\":\"2026-11-09T15:00:00Z\"}"
                    )
            );

        HoldConfirmation c = adapter.confirmHold("hold-1", "user-42", "Zoe", "Camus");

        assertThat(c.reservationProcessId()).isEqualTo("process-1");
        assertThat(c.expiresAt()).isNotNull();
    }

    @Test
    void traduceUnHoldVencidoANoEncontrado() {
        server
            .expect(requestTo(containsString("/api/appointment-holds/hold-1/confirm")))
            .andRespond(
                withStatus(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body("{\"status\":404,\"detail\":\"Hold expired\",\"code\":\"HOLD_EXPIRED\"}")
            );

        assertThatThrownBy(() -> adapter.confirmHold("hold-1", "user-42", "Zoe", "Camus")).isInstanceOfSatisfying(
            BusinessException.class,
            e -> {
                assertThat(e.getCode()).isEqualTo("HOLD_EXPIRED");
                assertThat(e.getKind()).isEqualTo(ErrorKind.NOT_FOUND);
            }
        );
    }

    @Test
    void siLaCatedraFallaDevuelveErrorDeDisponibilidad() {
        server.expect(requestTo(containsString("/api/appointment-holds"))).andRespond(withServerError());

        assertThatThrownBy(() ->
            adapter.createHold(201L, LocalDate.of(2026, 11, 9), LocalTime.of(12, 30), "user-42")
        ).isInstanceOfSatisfying(BusinessException.class, e -> assertThat(e.getKind()).isEqualTo(ErrorKind.UNAVAILABLE));
    }
}
