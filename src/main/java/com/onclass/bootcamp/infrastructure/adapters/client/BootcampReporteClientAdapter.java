package com.onclass.bootcamp.infrastructure.adapters.client;

import com.onclass.bootcamp.domain.spi.BootcampReporteClientPort;
import com.onclass.bootcamp.infrastructure.adapters.util.ClientConstants;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampReporteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BootcampReporteClientAdapter implements BootcampReporteClientPort {

    private final WebClient webClient;

    public BootcampReporteClientAdapter(WebClient.Builder webClientBuilder,
                                        @Value(ClientConstants.SERVICES_REPORTE_URL_PROPERTY) String reporteUrl) {
        this.webClient = webClientBuilder.baseUrl(reporteUrl).build();
    }

    @Override
    public Mono<Void> notificarNuevoBootcamp(BootcampReporteDTO reporteDTO) {
        return webClient.post()
                .uri(ClientConstants.REPORTE_BOOTCAMPS_ENDPOINT)
                .bodyValue(reporteDTO)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
