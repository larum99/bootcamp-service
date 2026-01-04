package com.onclass.bootcamp.infrastructure.adapters.client;

import com.onclass.bootcamp.domain.spi.TecnologiaClientPort;
import com.onclass.bootcamp.infrastructure.adapters.util.ClientConstants;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TecnologiaClientAdapter implements TecnologiaClientPort {

    private final WebClient webClient;

    public TecnologiaClientAdapter(WebClient.Builder webClientBuilder,
                                   @Value(ClientConstants.SERVICES_TECNOLOGIA_URL_PROPERTY) String tecnologiaUrl) {
        this.webClient = webClientBuilder
                .baseUrl(tecnologiaUrl)
                .build();
    }

    @Override
    public Mono<Void> eliminarTecnologiasPorCapacidades(List<Long> capacidadIds) {
        return webClient.method(HttpMethod.DELETE)
                .uri(ClientConstants.CAPACIDAD_TECNOLOGIAS_DELETE_BY_CAPACIDADES_ENDPOINT)
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .body(BodyInserters.fromValue(capacidadIds))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Flux<Long> findTecnologiaIdsByCapacidades(List<Long> capacidadIds) {
        return webClient.post()
                .uri(ClientConstants.CAPACIDAD_TECNOLOGIAS_BY_CAPACIDADES_ENDPOINT)
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .body(BodyInserters.fromValue(capacidadIds))
                .retrieve()
                .bodyToFlux(Long.class);
    }

    @Override
    public Mono<Integer> countCapacidadesByTecnologiaId(Long tecnologiaId) {
        return webClient.get()
                .uri(ClientConstants.CAPACIDAD_TECNOLOGIAS_COUNT_BY_TECNOLOGIA_ENDPOINT, tecnologiaId)
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .retrieve()
                .bodyToMono(Integer.class)
                .defaultIfEmpty(0);
    }

    @Override
    public Mono<Void> eliminarTecnologiaPorId(Long tecnologiaId) {
        return webClient.delete()
                .uri(ClientConstants.TECNOLOGIAS_DELETE_BY_ID_ENDPOINT, tecnologiaId)
                .header(Constants.X_MESSAGE_ID, ClientConstants.MESSAGE_ID_VALUE)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
