package ar.edu.um.prog2.camus.turnos.error;

/**
 * Error de negocio con un código estable (por ejemplo PROFESSIONAL_NOT_FOUND).
 * Los slices la extienden o la lanzan; el ExceptionTranslator la convierte en
 * problem+json con el campo "code".
 */
public class BusinessException extends RuntimeException {

    private final String code;
    private final ErrorKind kind;

    public BusinessException(String code, ErrorKind kind, String message) {
        super(message);
        this.code = code;
        this.kind = kind;
    }

    public String getCode() {
        return code;
    }

    public ErrorKind getKind() {
        return kind;
    }
}
