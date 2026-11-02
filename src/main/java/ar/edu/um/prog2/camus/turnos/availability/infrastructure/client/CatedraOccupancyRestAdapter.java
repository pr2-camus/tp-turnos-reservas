package ar.edu.um.prog2.camus.turnos.availability.infrastructure.client;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Occupancy;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.OccupancyStatus;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.out.OccupancyClient;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Consulta las ocupaciones a la catedra con el JWT tecnico del proyecto. */
@Component
public class CatedraOccupancyRestAdapter implements OccupancyClient {

    private final RestClient restClient;

    public CatedraOccupancyRestAdapter(@Qualifier("catedraRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Occupancy> getOccupancies(long professionalId, LocalDate from, LocalDate to) {
        try {
            OccupancyResponse response = restClient
                .get()
                .uri(uri ->
                    uri
                        .path("/api/appointment-occupancies")
                        .queryParam("professionalId", professionalId)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build()
                )
                .retrieve()
                .body(OccupancyResponse.class);
            if (response == null || response.occupancies() == null) {
                return List.of();
            }
            return response.occupancies().stream().map(this::toDomain).toList();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BusinessException("CATEDRA_BAD_REQUEST", ErrorKind.VALIDATION, "La catedra rechazo la consulta de ocupaciones.");
        } catch (RestClientException e) {
            throw new BusinessException("CATEDRA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "El servicio de la catedra no esta disponible.");
        }
    }

    private Occupancy toDomain(OccupancyDto o) {
        return new Occupancy(
            LocalDate.parse(o.date()),
            LocalTime.parse(o.startTime()),
            OccupancyStatus.valueOf(o.status()),
            o.expiresAt() == null ? null : Instant.parse(o.expiresAt())
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OccupancyResponse(List<OccupancyDto> occupancies) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OccupancyDto(String date, String startTime, String status, String expiresAt) {}
}
