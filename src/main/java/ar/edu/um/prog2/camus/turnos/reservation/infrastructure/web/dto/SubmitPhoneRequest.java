package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitPhoneRequest(@NotBlank @Size(max = 30) String phoneNumber) {}
