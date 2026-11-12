package ar.edu.um.prog2.camus.turnos.shared.infrastructure.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

class RetryTest {

    @Test
    void reintentaHastaQueFunciona() {
        AtomicInteger calls = new AtomicInteger();

        String result = Retry.withBackoff(3, Duration.ZERO, () -> {
            if (calls.incrementAndGet() < 3) {
                throw new ResourceAccessException("sin respuesta");
            }
            return "ok";
        });

        assertThat(result).isEqualTo("ok");
        assertThat(calls.get()).isEqualTo(3);
    }

    @Test
    void seRindeAlLlegarAlMaximoDeIntentos() {
        AtomicInteger calls = new AtomicInteger();

        assertThatThrownBy(() ->
            Retry.withBackoff(3, Duration.ZERO, () -> {
                calls.incrementAndGet();
                throw new ResourceAccessException("sin respuesta");
            })
        ).isInstanceOf(ResourceAccessException.class);

        assertThat(calls.get()).isEqualTo(3);
    }

    @Test
    void noReintentaUnErrorDelCliente() {
        AtomicInteger calls = new AtomicInteger();

        assertThatThrownBy(() ->
            Retry.withBackoff(3, Duration.ZERO, () -> {
                calls.incrementAndGet();
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            })
        ).isInstanceOf(HttpClientErrorException.class);

        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void reintentaUnErrorDelServidor() {
        AtomicInteger calls = new AtomicInteger();

        assertThatThrownBy(() ->
            Retry.withBackoff(2, Duration.ZERO, () -> {
                calls.incrementAndGet();
                throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);
            })
        ).isInstanceOf(HttpServerErrorException.class);

        assertThat(calls.get()).isEqualTo(2);
    }
}
