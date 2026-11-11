package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

public record CancelReservationRequest(@Size(max = 500) String reason) {}
