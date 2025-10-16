package com.onclass.bootcamp.infrastructure.adapters.client;

import com.onclass.bootcamp.domain.spi.TecnologiaClientPort;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
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
                .uri("/tecnologias/by-capacidades")
                .header(Constants.X_MESSAGE_ID, "12345")
                .body(BodyInserters.fromValue(capacidadIds))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
