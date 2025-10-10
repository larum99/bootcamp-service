package com.onclass.bootcamp.infrastructure.entrypoints.handler;

import com.onclass.bootcamp.domain.api.BootcampServicePort;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.exceptions.TechnicalException;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.mapper.BootcampMapper;
import com.onclass.bootcamp.infrastructure.entrypoints.util.APIResponse;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import com.onclass.bootcamp.infrastructure.entrypoints.util.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BootcampHandlerImpl {

    private static final Logger log = LoggerFactory.getLogger(BootcampHandlerImpl.class);

    private final BootcampServicePort bootcampServicePort;
    private final BootcampMapper bootcampMapper;

    public BootcampHandlerImpl(BootcampServicePort bootcampServicePort,
                               BootcampMapper bootcampMapper) {
        this.bootcampServicePort = bootcampServicePort;
        this.bootcampMapper = bootcampMapper;
    }

    public Mono<ServerResponse> createBootcamp(ServerRequest request) {
        String messageId = getMessageId(request);

        return request.bodyToMono(BootcampDTO.class)
                .flatMap(dto -> bootcampServicePort
                        .registrarBootcamp(bootcampMapper.toModel(dto), messageId)
                        .doOnSuccess(saved -> log.info("Bootcamp creado con messageId: {}", messageId)))
                .flatMap(saved -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.BOOTCAMP_CREATED.getDescription()))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(Constants.BOOTCAMP_ERROR, ex))
                .onErrorResume(ex -> handleErrors(ex, messageId));
    }

    private Mono<ServerResponse> handleErrors(Throwable ex, String messageId) {
        log.error("Error procesando solicitud con messageId: {}", messageId, ex);

        if (ex instanceof BusinessException businessEx) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    messageId,
                    businessEx.getTechnicalMessage(),
                    List.of(ErrorDTO.builder()
                            .code(businessEx.getTechnicalMessage().getCode())
                            .message(businessEx.getTechnicalMessage().getDescription())
                            .param(businessEx.getTechnicalMessage().getParam())
                            .build()));
        }

        if (ex instanceof TechnicalException techEx) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    messageId,
                    techEx.getTechnicalMessage(),
                    List.of(ErrorDTO.builder()
                            .code(techEx.getTechnicalMessage().getCode())
                            .message(techEx.getTechnicalMessage().getDescription())
                            .param(techEx.getTechnicalMessage().getParam())
                            .build()));
        }

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageId,
                TechnicalMessage.INTERNAL_ERROR,
                List.of(ErrorDTO.builder()
                        .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                        .message(TechnicalMessage.INTERNAL_ERROR.getDescription())
                        .build()));
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier,
                                                    TechnicalMessage error, List<ErrorDTO> errors) {
        APIResponse apiErrorResponse = APIResponse.builder()
                .code(error.getCode())
                .message(error.getDescription())
                .identifier(identifier)
                .date(Instant.now().toString())
                .errors(errors)
                .build();
        return ServerResponse.status(httpStatus).bodyValue(apiErrorResponse);
    }

    private String getMessageId(ServerRequest serverRequest) {
        return Optional.ofNullable(serverRequest.headers().firstHeader(Constants.X_MESSAGE_ID))
                .orElse(UUID.randomUUID().toString());
    }
}
