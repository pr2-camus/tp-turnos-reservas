package ar.edu.um.prog2.camus.turnos.shared.infrastructure.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catedra")
public record CatedraProperties(String apiUrl, String jwt, Duration connectTimeout, Duration readTimeout) {
    public CatedraProperties {
        if (connectTimeout == null) connectTimeout = Duration.ofSeconds(3);
        if (readTimeout == null) readTimeout = Duration.ofSeconds(10);
    }
}
