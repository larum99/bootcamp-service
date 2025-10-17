package com.onclass.bootcamp.infrastructure.adapters.client;

import com.onclass.bootcamp.domain.spi.TecnologiaClientPort;
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
                                   @Value("${services.tecnologia.url}") String tecnologiaUrl) {
        this.webClient = webClientBuilder
                .baseUrl(tecnologiaUrl)
                .build();
    }

    @Override
    public Mono<Void> eliminarTecnologiasPorCapacidades(List<Long> capacidadIds) {
        return webClient.method(HttpMethod.DELETE)
                .uri("/capacidad-tecnologias/by-capacidades")
                .header(Constants.X_MESSAGE_ID, "12345")
                .body(BodyInserters.fromValue(capacidadIds))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Flux<Long> findTecnologiaIdsByCapacidades(List<Long> capacidadIds) {
        return webClient.post()
                .uri("/capacidad-tecnologias/tecnologias/by-capacidades")
                .header(Constants.X_MESSAGE_ID, "12345")
                .body(BodyInserters.fromValue(capacidadIds))
                .retrieve()
                .bodyToFlux(Long.class);
    }

    @Override
    public Mono<Integer> countCapacidadesByTecnologiaId(Long tecnologiaId) {
        return webClient.get()
                .uri("/capacidad-tecnologias/count/by-tecnologia/{id}", tecnologiaId)
                .header(Constants.X_MESSAGE_ID, "12345")
                .retrieve()
                .bodyToMono(Integer.class)
                .defaultIfEmpty(0);
    }

    @Override
    public Mono<Void> eliminarTecnologiaPorId(Long tecnologiaId) {
        return webClient.delete()
                .uri("/tecnologias/{id}", tecnologiaId)
                .header(Constants.X_MESSAGE_ID, "12345")
                .retrieve()
                .bodyToMono(Void.class);
    }
}
