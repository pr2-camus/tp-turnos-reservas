package ar.edu.um.prog2.camus.turnos.error;

/**
 * Tipo de error de negocio. Permite traducirlo a un estado HTTP
 * sin que el dominio conozca nada de HTTP.
 */
public enum ErrorKind {
    VALIDATION,
    NOT_FOUND,
    CONFLICT,
    UNAVAILABLE,
}
