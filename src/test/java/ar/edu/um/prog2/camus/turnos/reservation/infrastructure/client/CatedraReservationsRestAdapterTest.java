package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CatedraReservationsRestAdapterTest {

    private MockRestServiceServer server;
    private CatedraReservationsRestAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://catedra.test");
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new CatedraReservationsRestAdapter(builder.build());
    }

    private static String items(int count, String prefix) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"reservationProcessId\":\"").append(prefix).append(i).append("\",\"status\":\"CONFIRMED\"}");
        }
        return sb.append(']').toString();
    }

    @Test
    void leeUnaPaginaYMapeaLosEstados() {
        server
            .expect(requestTo(containsString("page=0")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    "[{\"reservationProcessId\":\"p1\",\"status\":\"CONFIRMED\"},{\"reservationProcessId\":\"p2\",\"status\":\"FAILED\"}]",
                    MediaType.APPLICATION_JSON
                )
            );

        List<CatedraReservation> result = adapter.listReservations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).toString()).contains("p1").contains("CONFIRMED");
        assertThat(result.get(1).toString()).contains("p2").contains("FAILED");
        server.verify();
    }

    @Test
    void siLaPaginaVieneCompletaPideLaSiguiente() {
        server.expect(requestTo(containsString("page=0"))).andRespond(withSuccess(items(100, "a"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(containsString("page=1"))).andRespond(withSuccess(items(1, "b"), MediaType.APPLICATION_JSON));

        assertThat(adapter.listReservations()).hasSize(101);
        server.verify();
    }

    @Test
    void sinReservasDevuelveListaVacia() {
        server.expect(requestTo(containsString("page=0"))).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertThat(adapter.listReservations()).isEmpty();
    }

    @Test
    void reintentaSiElPrimerIntentoFalla() {
        server.expect(requestTo(containsString("page=0"))).andRespond(withServerError());
        server.expect(requestTo(containsString("page=0"))).andRespond(withSuccess(items(1, "r"), MediaType.APPLICATION_JSON));

        assertThat(adapter.listReservations()).hasSize(1);
        server.verify();
    }

    @Test
    void despuesDeTresIntentosFallidosDevuelveErrorDeDisponibilidad() {
        server.expect(times(3), requestTo(containsString("page=0"))).andRespond(withServerError());

        assertThatThrownBy(() -> adapter.listReservations()).isInstanceOfSatisfying(BusinessException.class, e -> {
            assertThat(e.getCode()).isEqualTo("CATEDRA_UNAVAILABLE");
            assertThat(e.getKind()).isEqualTo(ErrorKind.UNAVAILABLE);
        });
        server.verify();
    }

    @Test
    void unErrorDelCliente4xxNoSeReintenta() {
        server.expect(requestTo(containsString("page=0"))).andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> adapter.listReservations()).isInstanceOf(BusinessException.class);
        server.verify();
    }
}
