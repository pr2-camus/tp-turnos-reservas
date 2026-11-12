package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.repository;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.entity.ReservationProcessEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationProcessRepository extends JpaRepository<ReservationProcessEntity, Long> {
    Optional<ReservationProcessEntity> findByReservationProcessId(String reservationProcessId);

    List<ReservationProcessEntity> findByUserId(Long userId);

    List<ReservationProcessEntity> findByStatusIn(Collection<ReservationStatus> statuses);
}
