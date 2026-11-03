package ar.edu.um.prog2.camus.turnos.availability.infrastructure.web.controller;

import ar.edu.um.prog2.camus.turnos.availability.application.service.AvailabilityService;
import ar.edu.um.prog2.camus.turnos.availability.infrastructure.web.dto.SlotResponse;
import ar.edu.um.prog2.camus.turnos.availability.infrastructure.web.mapper.SlotDtoMapper;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final SlotDtoMapper slotDtoMapper;

    public AvailabilityController(AvailabilityService availabilityService, SlotDtoMapper slotDtoMapper) {
        this.availabilityService = availabilityService;
        this.slotDtoMapper = slotDtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<SlotResponse>> getAvailability(
        @RequestParam long professionalId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
            availabilityService.getAvailability(professionalId, date).stream().map(slotDtoMapper::toResponse).toList()
        );
    }
}
