package ar.edu.um.prog2.camus.turnos.availability.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Occupancy;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.OccupancyStatus;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CatedraOccupancyRestAdapterTest {

    private static final String JSON = """
    {"professionalId":201,"from":"2026-11-02","to":"2026-11-16","occupancies":[
      {"date":"2026-11-09","startTime":"12:00:00","status":"CONFIRMED","expiresAt":null},
      {"date":"2026-11-09","startTime":"12:30:00","status":"HELD","expiresAt":"2026-11-02T20:00:00Z"}
    ],"campoNuevo":"se ignora"}
    """;

    @Test
    void mapeaOcupacionesConfirmadasYRetenidas() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://catedra.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(containsString("/api/appointment-occupancies"))).andRespond(withSuccess(JSON, MediaType.APPLICATION_JSON));
        CatedraOccupancyRestAdapter adapter = new CatedraOccupancyRestAdapter(builder.build());

        List<Occupancy> result = adapter.getOccupancies(201L, LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 16));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo(OccupancyStatus.CONFIRMED);
        assertThat(result.get(0).startTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(result.get(0).expiresAt()).isNull();
        assertThat(result.get(1).status()).isEqualTo(OccupancyStatus.HELD);
        assertThat(result.get(1).expiresAt()).isNotNull();
        server.verify();
    }

    @Test
    void siLaCatedraFallaDevuelveErrorDeDisponibilidad() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://catedra.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(containsString("/api/appointment-occupancies"))).andRespond(withServerError());
        CatedraOccupancyRestAdapter adapter = new CatedraOccupancyRestAdapter(builder.build());

        assertThatThrownBy(() ->
            adapter.getOccupancies(201L, LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 16))
        ).isInstanceOfSatisfying(BusinessException.class, e -> assertThat(e.getKind()).isEqualTo(ErrorKind.UNAVAILABLE));
    }
}
