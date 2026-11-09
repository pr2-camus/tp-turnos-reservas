package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.mapper;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.entity.ReservationProcessEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationProcessMapper {

    public ReservationProcessEntity toEntity(ReservationProcess p) {
        ReservationProcessEntity e = new ReservationProcessEntity();
        e.setId(p.getId());
        e.setUserId(p.getUserId());
        e.setHoldId(p.getHoldId());
        e.setReservationProcessId(p.getReservationProcessId());
        e.setRequestEventId(p.getRequestEventId());
        e.setProfessionalId(p.getProfessionalId());
        e.setDate(p.getDate());
        e.setStartTime(p.getStartTime());
        e.setExpiresAt(p.getExpiresAt());
        e.setStatus(p.getStatus());
        return e;
    }

    public ReservationProcess toDomain(ReservationProcessEntity e) {
        return ReservationProcess.restore(
            e.getId(),
            e.getUserId(),
            e.getHoldId(),
            e.getReservationProcessId(),
            e.getRequestEventId(),
            e.getProfessionalId(),
            e.getDate(),
            e.getStartTime(),
            e.getExpiresAt(),
            e.getStatus()
        );
    }
}
