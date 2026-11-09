package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalTime;

/** Guarda la hora del turno como texto HH:mm, para que no se corra por la zona horaria. */
@Converter
public class LocalTimeStringConverter implements AttributeConverter<LocalTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalTime time) {
        return time == null ? null : time.toString();
    }

    @Override
    public LocalTime convertToEntityAttribute(String text) {
        return text == null ? null : LocalTime.parse(text);
    }
}
