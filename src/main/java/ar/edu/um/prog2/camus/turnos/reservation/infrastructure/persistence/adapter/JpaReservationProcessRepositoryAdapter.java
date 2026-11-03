package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.adapter;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.mapper.ReservationProcessMapper;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.repository.JpaReservationProcessRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaReservationProcessRepositoryAdapter implements ReservationProcessRepository {

    private final JpaReservationProcessRepository jpaRepository;
    private final ReservationProcessMapper mapper;

    public JpaReservationProcessRepositoryAdapter(JpaReservationProcessRepository jpaRepository, ReservationProcessMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ReservationProcess save(ReservationProcess process) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(process)));
    }

    @Override
    public Optional<ReservationProcess> findByReservationProcessId(String reservationProcessId) {
        return jpaRepository.findByReservationProcessId(reservationProcessId).map(mapper::toDomain);
    }

    @Override
    public List<ReservationProcess> findAllByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
