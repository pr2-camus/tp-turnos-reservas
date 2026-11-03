package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.client;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldConfirmation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldCreated;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Crea y confirma holds en la catedra con el JWT tecnico. No reintenta: son operaciones con efectos. */
@Component
public class CatedraHoldRestAdapter implements HoldClient {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final RestClient restClient;

    public CatedraHoldRestAdapter(@Qualifier("catedraRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public HoldCreated createHold(long professionalId, LocalDate date, LocalTime startTime, String externalPatientId) {
        try {
            HoldResponse r = restClient
                .post()
                .uri("/api/appointment-holds")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new HoldRequest(professionalId, date.toString(), startTime.format(HH_MM), externalPatientId))
                .retrieve()
                .body(HoldResponse.class);
            if (r == null) {
                throw emptyResponse();
            }
            return new HoldCreated(r.holdId(), r.reservationProcessId(), Instant.parse(r.expiresAt()));
        } catch (RestClientException e) {
            throw translate(e);
        }
    }

    @Override
    public HoldConfirmation confirmHold(String holdId, String externalPatientId, String firstName, String lastName) {
        try {
            ConfirmResponse r = restClient
                .post()
                .uri("/api/appointment-holds/{holdId}/confirm", holdId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ConfirmRequest(externalPatientId, firstName, lastName))
                .retrieve()
                .body(ConfirmResponse.class);
            if (r == null) {
                throw emptyResponse();
            }
            return new HoldConfirmation(r.reservationProcessId(), Instant.parse(r.expiresAt()));
        } catch (RestClientException e) {
            throw translate(e);
        }
    }

    private BusinessException emptyResponse() {
        return new BusinessException("CATEDRA_BAD_RESPONSE", ErrorKind.UNAVAILABLE, "La catedra respondio sin contenido.");
    }

    /** Traduce el error de la catedra por status y code. Sin respuesta (timeout, caida) es UNAVAILABLE. */
    private BusinessException translate(RestClientException e) {
        if (e instanceof HttpStatusCodeException http) {
            CatedraError error = readError(http);
            int status = http.getStatusCode().value();
            String code = error != null && error.code() != null ? error.code() : "CATEDRA_ERROR";
            String detail = error != null && error.detail() != null ? error.detail() : "La catedra rechazo el pedido.";
            return new BusinessException(code, kindFor(status), detail);
        }
        return new BusinessException("CATEDRA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "El servicio de la catedra no esta disponible.");
    }

    private CatedraError readError(HttpStatusCodeException e) {
        try {
            return e.getResponseBodyAs(CatedraError.class);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private ErrorKind kindFor(int status) {
        return switch (status) {
            case 400 -> ErrorKind.VALIDATION;
            case 404 -> ErrorKind.NOT_FOUND;
            case 409 -> ErrorKind.CONFLICT;
            default -> ErrorKind.UNAVAILABLE;
        };
    }

    public record HoldRequest(long professionalId, String date, String startTime, String externalPatientId) {}

    public record ConfirmRequest(String externalPatientId, String patientFirstName, String patientLastName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record HoldResponse(String holdId, String reservationProcessId, String expiresAt) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ConfirmResponse(String reservationProcessId, String expiresAt) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CatedraError(String code, String detail) {}
}
