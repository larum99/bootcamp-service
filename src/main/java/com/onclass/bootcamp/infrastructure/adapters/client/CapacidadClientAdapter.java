package com.onclass.bootcamp.infrastructure.adapters.client;

import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import com.onclass.bootcamp.domain.utils.CapacidadSummary;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampCapacidadDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadSummaryDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CapacidadClientAdapter implements CapacidadClientPort {

    private final WebClient webClient;

    public CapacidadClientAdapter(WebClient.Builder webClientBuilder,
                                  @Value("${services.capacidad.url}") String capacidadUrl) {
        this.webClient = webClientBuilder
                .baseUrl(capacidadUrl)
                .build();
    }

    @Override
    public Mono<Void> associateBootcampWithCapacidades(Long bootcampId, List<Long> capacidadesIds) {
        return Flux.fromIterable(capacidadesIds)
                .map(capacidadId -> new BootcampCapacidadDTO(bootcampId, capacidadId))
                .collectList()
                .flatMap(dtos ->
                        webClient.post()
                                .uri("/capacidad-bootcamps")
                                .header(Constants.X_MESSAGE_ID, "12345")
                                .bodyValue(dtos)
                                .retrieve()
                                .bodyToMono(Void.class)
                );
    }

    @Override
    public Flux<CapacidadSummary> findCapacidadesByBootcampId(Long bootcampId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/capacidad-bootcamps/{bootcampId}/capacidades")
                        .build(bootcampId))
                .header(Constants.X_MESSAGE_ID, "12345")
                .retrieve()
                .bodyToFlux(CapacidadSummaryDTO.class)
                .map(dto -> new CapacidadSummary(dto.id(), dto.nombre()));
    }
}
