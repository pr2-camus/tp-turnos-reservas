package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.mapper;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto.ReservationProcessResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationDtoMapper {

    public ReservationProcessResponse toResponse(ReservationProcess p) {
        return new ReservationProcessResponse(
            p.getReservationProcessId(),
            p.getStatus().name(),
            p.getExpiresAt().toString(),
            p.getProfessionalId(),
            p.getDate().toString(),
            p.getStartTime().toString()
        );
    }
}
