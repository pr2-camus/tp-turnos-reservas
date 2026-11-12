package ar.edu.um.prog2.camus.turnos.availability.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.ProfessionalAgenda;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CatalogRestAdapterTest {

    private MockRestServiceServer server;
    private CatalogRestAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://catalogo.test");
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new CatalogRestAdapter(builder.build());
        Jwt jwt = Jwt.withTokenValue("token-123").header("alg", "none").claim("sub", "user-42").build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void obtieneLaAgendaYReenviaElJwt() {
        server
            .expect(requestTo(containsString("/api/professionals/204/agenda")))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer token-123"))
            .andRespond(
                withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                        "{\"professional\":{\"id\":204,\"categoryId\":1,\"firstName\":\"Ana\",\"lastName\":\"Lopez\",\"enabled\":true}," +
                            "\"weeklySchedules\":[{\"id\":1,\"professionalId\":204,\"dayOfWeek\":\"MONDAY\"," +
                            "\"startTime\":\"09:00\",\"endTime\":\"12:00\",\"slotDurationMinutes\":30,\"enabled\":true}]}"
                    )
            );

        Optional<ProfessionalAgenda> agenda = adapter.getAgenda(204L);

        assertThat(agenda).isPresent();
        assertThat(agenda.orElseThrow().toString()).contains("Ana").contains("MONDAY");
        server.verify();
    }

    @Test
    void siElProfesionalNoExisteDevuelveVacio() {
        server.expect(requestTo(containsString("/api/professionals/999/agenda"))).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(adapter.getAgenda(999L)).isEmpty();
    }

    @Test
    void siElCatalogoFallaDevuelveErrorDeDisponibilidad() {
        server.expect(requestTo(containsString("/api/professionals/204/agenda"))).andRespond(withServerError());

        assertThatThrownBy(() -> adapter.getAgenda(204L)).isInstanceOfSatisfying(BusinessException.class, e -> {
            assertThat(e.getCode()).isEqualTo("CATALOG_UNAVAILABLE");
            assertThat(e.getKind()).isEqualTo(ErrorKind.UNAVAILABLE);
        });
    }

    @Test
    void sinJwtEnElContextoFallaConEstadoIlegal() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> adapter.getAgenda(204L)).isInstanceOf(IllegalStateException.class);
    }
}
