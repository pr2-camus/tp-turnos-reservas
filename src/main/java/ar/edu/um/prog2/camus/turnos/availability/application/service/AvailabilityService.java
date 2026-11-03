package ar.edu.um.prog2.camus.turnos.availability.application.service;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.in.GetAvailabilityUseCase;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final GetAvailabilityUseCase getAvailabilityUseCase;

    public AvailabilityService(GetAvailabilityUseCase getAvailabilityUseCase) {
        this.getAvailabilityUseCase = getAvailabilityUseCase;
    }

    public List<Slot> getAvailability(long professionalId, LocalDate date) {
        return getAvailabilityUseCase
            .getAvailability(professionalId, date)
            .orElseThrow(() ->
                new BusinessException("PROFESSIONAL_NOT_FOUND", ErrorKind.NOT_FOUND, "No existe el profesional " + professionalId + ".")
            );
    }
}
