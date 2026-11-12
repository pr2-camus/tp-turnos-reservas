package ar.edu.um.prog2.camus.turnos.shared.infrastructure.retry;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

/**
 * Reintentos acotados con espera que se duplica. Usar solo en operaciones seguras de repetir
 * (lecturas o acciones idempotentes). Solo se reintentan fallos transitorios.
 */
public final class Retry {

    private static final Logger log = LoggerFactory.getLogger(Retry.class);

    private Retry() {}

    public static <T> T withBackoff(int maxAttempts, Duration initialDelay, Supplier<T> action) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts debe ser al menos 1");
        }
        Duration delay = initialDelay;
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                if (!isTransient(e)) {
                    throw e;
                }
                last = e;
                if (attempt < maxAttempts) {
                    log.warn("Intento {} de {} fallo ({}). Reintento en {} ms.", attempt, maxAttempts, e.getMessage(), delay.toMillis());
                    sleep(delay);
                    delay = delay.multipliedBy(2);
                }
            }
        }
        throw Objects.requireNonNull(last);
    }

    /** Transitorio: sin respuesta (timeout o caida), 5xx o 429. Los 4xx no se reintentan. */
    static boolean isTransient(RuntimeException e) {
        if (e instanceof HttpStatusCodeException http) {
            int status = http.getStatusCode().value();
            return status >= 500 || status == 429;
        }
        return e instanceof RestClientException;
    }

    private static void sleep(Duration delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Reintento interrumpido", ex);
        }
    }
}
