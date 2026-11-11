package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.controller;

import ar.edu.um.prog2.camus.turnos.reservation.application.service.ReservationService;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto.CancelReservationRequest;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto.ReservationProcessResponse;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto.StartReservationRequest;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto.SubmitPhoneRequest;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.mapper.ReservationDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationDtoMapper mapper;

    public ReservationController(ReservationService reservationService, ReservationDtoMapper mapper) {
        this.reservationService = reservationService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ReservationProcessResponse> start(@Valid @RequestBody StartReservationRequest request) {
        ReservationProcess process = reservationService.startReservation(request.professionalId(), request.date(), request.startTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(process));
    }

    @PostMapping("/{id}/phone")
    public ResponseEntity<ReservationProcessResponse> submitPhone(@PathVariable String id, @Valid @RequestBody SubmitPhoneRequest request) {
        ReservationProcess process = reservationService.submitPhone(id, request.phoneNumber());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toResponse(process));
    }

    @GetMapping
    public List<ReservationProcessResponse> listMine() {
        return reservationService.listMyReservations().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ReservationProcessResponse get(@PathVariable String id) {
        return mapper.toResponse(reservationService.getReservation(id));
    }

    @PostMapping("/{id}/cancel")
    public ReservationProcessResponse cancel(
        @PathVariable String id,
        @Valid @RequestBody(required = false) CancelReservationRequest request
    ) {
        String reason = request == null ? null : request.reason();
        return mapper.toResponse(reservationService.cancelReservation(id, reason));
    }
}
