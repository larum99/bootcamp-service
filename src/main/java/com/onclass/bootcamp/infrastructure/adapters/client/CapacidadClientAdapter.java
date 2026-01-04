package com.onclass.bootcamp.infrastructure.adapters.client;

import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import com.onclass.bootcamp.domain.utils.CapacidadSummary;
import com.onclass.bootcamp.infrastructure.adapters.util.ClientConstants;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampCapacidadDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadSummaryDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CapacidadClientAdapter implements CapacidadClientPort {

    private final WebClient webClient;

    public CapacidadClientAdapter(WebClient.Builder webClientBuilder,
                                  @Value(ClientConstants.SERVICES_CAPACIDAD_URL_PROPERTY) String capacidadUrl) {
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
                                .uri(ClientConstants.CAPACIDAD_BOOTCAMPS_ENDPOINT)
                                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                                .bodyValue(dtos)
                                .retrieve()
                                .bodyToMono(Void.class)
                );
    }

    @Override
    public Flux<CapacidadSummary> findCapacidadesByBootcampId(Long bootcampId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientConstants.CAPACIDAD_BOOTCAMPS_BY_ID_ENDPOINT)
                        .build(bootcampId))
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .retrieve()
                .bodyToFlux(CapacidadSummaryDTO.class)
                .map(dto -> new CapacidadSummary(dto.id(), dto.nombre()));
    }

    @Override
    public Mono<List<Long>> eliminarCapacidadesPorBootcamp(Long bootcampId) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientConstants.CAPACIDAD_BOOTCAMPS_DELETE_ENDPOINT)
                        .build(bootcampId))
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .retrieve()
                .bodyToFlux(Long.class)
                .collectList();
    }

    @Override
    public Mono<Integer> countBootcampsByCapacidadId(Long capacidadId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientConstants.CAPACIDADES_COUNT_BOOTCAMPS_ENDPOINT)
                        .build(capacidadId))
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .retrieve()
                .bodyToMono(Integer.class);
    }


    @Override
    public Mono<Void> eliminarCapacidadesPorIds(List<Long> capacidadIds) {
        return webClient.method(HttpMethod.DELETE)
                .uri(ClientConstants.CAPACIDADES_DELETE_ENDPOINT)
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .bodyValue(capacidadIds)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Boolean> validateCapacidadesExist(List<Long> capacidadesIds) {
        return webClient.post()
                .uri(ClientConstants.CAPACIDADES_VALIDATE_ENDPOINT)
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .bodyValue(capacidadesIds)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
