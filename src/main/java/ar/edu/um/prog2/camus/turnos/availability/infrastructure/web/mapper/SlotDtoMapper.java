package ar.edu.um.prog2.camus.turnos.availability.infrastructure.web.mapper;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import ar.edu.um.prog2.camus.turnos.availability.infrastructure.web.dto.SlotResponse;
import org.springframework.stereotype.Component;

@Component
public class SlotDtoMapper {

    public SlotResponse toResponse(Slot slot) {
        return new SlotResponse(slot.date().toString(), slot.startTime().toString(), slot.endTime().toString());
    }
}
