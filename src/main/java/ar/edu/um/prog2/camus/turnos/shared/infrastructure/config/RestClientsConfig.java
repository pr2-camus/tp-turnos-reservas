package ar.edu.um.prog2.camus.turnos.shared.infrastructure.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** Clientes REST hacia la catedra y hacia el servicio de catalogo, con timeouts. */
@Configuration
@EnableConfigurationProperties({ CatedraProperties.class, CatalogProperties.class })
public class RestClientsConfig {

    @Bean("catedraRestClient")
    public RestClient catedraRestClient(CatedraProperties props) {
        return RestClient.builder()
            .baseUrl(props.apiUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.jwt())
            .requestFactory(factory(props.connectTimeout(), props.readTimeout()))
            .build();
    }

    @Bean("catalogRestClient")
    public RestClient catalogRestClient(CatalogProperties props) {
        return RestClient.builder().baseUrl(props.url()).requestFactory(factory(props.connectTimeout(), props.readTimeout())).build();
    }

    private static ClientHttpRequestFactory factory(Duration connect, Duration read) {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(connect).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(read);
        return factory;
    }
}
