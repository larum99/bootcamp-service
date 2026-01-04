package com.onclass.bootcamp.infrastructure.entrypoints.handler;

import com.onclass.bootcamp.domain.api.BootcampServicePort;
import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.exceptions.TechnicalException;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.mapper.BootcampMapper;
import com.onclass.bootcamp.infrastructure.entrypoints.util.APIResponse;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import com.onclass.bootcamp.infrastructure.entrypoints.util.ErrorDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.util.HandlerConstants;
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
                        .doOnSuccess(saved -> log.info(HandlerConstants.BOOTCAMP_CREATED_LOG, messageId))
                        .map(saved -> {
                            BootcampDTO responseDTO = bootcampMapper.toDTO(saved);
                            responseDTO.setCapacidades(dto.getCapacidades());
                            return APIResponse.builder()
                                    .code(TechnicalMessage.BOOTCAMP_CREATED.getCode())
                                    .message(TechnicalMessage.BOOTCAMP_CREATED.getDescription())
                                    .identifier(messageId)
                                    .date(Instant.now().toString())
                                    .data(responseDTO)
                                    .build();
                        }))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(Constants.BOOTCAMP_ERROR, ex))
                .onErrorResume(ex -> handleErrors(ex, messageId));
    }

    public Mono<ServerResponse> getBootcamps(ServerRequest request) {
        String messageId = getMessageId(request);

        int page = parseQueryParam(request, HandlerConstants.PAGE_PARAM, HandlerConstants.DEFAULT_PAGE);
        int size = parseQueryParam(request, HandlerConstants.SIZE_PARAM, HandlerConstants.DEFAULT_SIZE);
        String sortBy = request.queryParam(HandlerConstants.SORT_BY_PARAM).orElse(HandlerConstants.DEFAULT_SORT_BY);
        String sortOrder = request.queryParam(HandlerConstants.SORT_ORDER_PARAM).orElse(HandlerConstants.DEFAULT_SORT_ORDER);

        BootcampCriteria criteria = new BootcampCriteria();
        criteria.setPage(page);
        criteria.setSize(size);
        criteria.setSortBy(sortBy);
        criteria.setSortOrder(sortOrder);

        return bootcampServicePort.listarBootcamps(criteria)
                .flatMap(pageResult -> ServerResponse.ok().bodyValue(pageResult))
                .onErrorResume(ex -> handleErrors(ex, messageId))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, messageId));
    }

    public Mono<ServerResponse> deleteBootcamp(ServerRequest request) {
        String messageId = getMessageId(request);
        Long bootcampId = Long.valueOf(request.pathVariable(HandlerConstants.ID_PATH_VARIABLE));

        return bootcampServicePort.eliminarBootcamp(bootcampId)
                .then(Mono.fromCallable(() -> APIResponse.builder()
                        .code(TechnicalMessage.BOOTCAMP_DELETED.getCode())
                        .message(TechnicalMessage.BOOTCAMP_DELETED.getDescription())
                        .identifier(messageId)
                        .date(Instant.now().toString())
                        .build()))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, messageId))
                .onErrorResume(ex -> handleErrors(ex, messageId));
    }

    public Mono<ServerResponse> getBootcampById(ServerRequest request) {
        String messageId = getMessageId(request);
        Long id = Long.valueOf(request.pathVariable(HandlerConstants.ID_PATH_VARIABLE));
        return bootcampServicePort.obtenerBootcampPorId(id)
                .flatMap(bootcamp ->
                        ServerResponse.ok().bodyValue(bootcamp))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, messageId))
                .onErrorResume(ex -> handleErrors(ex, messageId));
    }

    private int parseQueryParam(ServerRequest request, String name, int defaultValue) {
        return request.queryParam(name)
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }


    private Mono<ServerResponse> handleErrors(Throwable ex, String messageId) {
        log.error(HandlerConstants.ERROR_PROCESSING_REQUEST_LOG, messageId, ex);

        if (ex instanceof BusinessException businessEx) {
            if (businessEx.getTechnicalMessage() == TechnicalMessage.BOOTCAMP_NOT_FOUND) {
                return buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        messageId,
                        businessEx.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(businessEx.getTechnicalMessage().getCode())
                                .message(businessEx.getTechnicalMessage().getDescription())
                                .param(businessEx.getTechnicalMessage().getParam())
                                .build()));
            }
            
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
