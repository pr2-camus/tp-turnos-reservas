package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.client;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CatedraReservationsClient;
import ar.edu.um.prog2.camus.turnos.shared.infrastructure.retry.Retry;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Lee las reservas de la catedra. Es una lectura, asi que se puede reintentar. Sin filtros de fecha. */
@Component
public class CatedraReservationsRestAdapter implements CatedraReservationsClient {

    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGES = 20;
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration FIRST_DELAY = Duration.ofMillis(300);

    private final RestClient restClient;

    public CatedraReservationsRestAdapter(@Qualifier("catedraRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<CatedraReservation> listReservations() {
        List<CatedraReservation> all = new ArrayList<>();
        try {
            for (int page = 0; page < MAX_PAGES; page++) {
                int current = page;
                AppointmentItem[] items = Retry.withBackoff(MAX_ATTEMPTS, FIRST_DELAY, () -> fetchPage(current));
                if (items == null || items.length == 0) {
                    break;
                }
                for (AppointmentItem item : items) {
                    all.add(new CatedraReservation(item.reservationProcessId(), CatedraReservationStatus.from(item.status())));
                }
                if (items.length < PAGE_SIZE) {
                    break;
                }
            }
        } catch (RestClientException e) {
            throw new BusinessException(
                "CATEDRA_UNAVAILABLE",
                ErrorKind.UNAVAILABLE,
                "No se pudieron consultar las reservas de la catedra."
            );
        }
        return all;
    }

    private AppointmentItem[] fetchPage(int page) {
        return restClient.get().uri("/api/appointments?page={page}&size={size}", page, PAGE_SIZE).retrieve().body(AppointmentItem[].class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AppointmentItem(String reservationProcessId, String status) {}
}
