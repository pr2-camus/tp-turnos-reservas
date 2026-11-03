package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record StartReservationRequest(@NotNull Long professionalId, @NotNull LocalDate date, @NotNull LocalTime startTime) {}
