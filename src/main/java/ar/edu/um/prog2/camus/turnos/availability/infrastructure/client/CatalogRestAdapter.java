package ar.edu.um.prog2.camus.turnos.availability.infrastructure.client;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Professional;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.ProfessionalAgenda;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.WeeklySchedule;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.out.CatalogClient;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Llama al servicio de catalogo reenviando el JWT del usuario que hace el pedido. */
@Component
public class CatalogRestAdapter implements CatalogClient {

    private final RestClient restClient;

    public CatalogRestAdapter(@Qualifier("catalogRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Optional<ProfessionalAgenda> getAgenda(long professionalId) {
        try {
            AgendaResponse response = restClient
                .get()
                .uri("/api/professionals/{id}/agenda", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + currentUserToken())
                .retrieve()
                .body(AgendaResponse.class);
            return Optional.ofNullable(response).map(this::toDomain);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new BusinessException("CATALOG_UNAVAILABLE", ErrorKind.UNAVAILABLE, "El servicio de catalogo no esta disponible.");
        }
    }

    private String currentUserToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getTokenValue();
        }
        throw new IllegalStateException("No hay un JWT de usuario en el contexto de seguridad.");
    }

    private ProfessionalAgenda toDomain(AgendaResponse r) {
        ProfessionalDto p = r.professional();
        Professional professional = new Professional(p.id(), p.categoryId(), p.firstName(), p.lastName(), p.enabled());
        List<WeeklySchedule> schedules = r
            .weeklySchedules()
            .stream()
            .map(h ->
                new WeeklySchedule(
                    h.id(),
                    h.professionalId(),
                    DayOfWeek.valueOf(h.dayOfWeek()),
                    LocalTime.parse(h.startTime()),
                    LocalTime.parse(h.endTime()),
                    h.slotDurationMinutes(),
                    h.enabled()
                )
            )
            .toList();
        return new ProfessionalAgenda(professional, schedules);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AgendaResponse(ProfessionalDto professional, List<ScheduleDto> weeklySchedules) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ProfessionalDto(Long id, Long categoryId, String firstName, String lastName, boolean enabled) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ScheduleDto(
        Long id,
        Long professionalId,
        String dayOfWeek,
        String startTime,
        String endTime,
        int slotDurationMinutes,
        boolean enabled
    ) {}
}
