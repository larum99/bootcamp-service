package com.onclass.bootcamp.application.configSwagger;

import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RouterOperation(
        path = ApiConstants.PATH_BOOTCAMP,
        beanClass = BootcampHandlerImpl.class,
        beanMethod = "createBootcamp",
        operation = @Operation(
                operationId = ApiConstants.CREATE_BOOTCAMP_OPERATION_ID,
                summary = ApiConstants.CREATE_BOOTCAMP_SUMMARY,
                requestBody = @RequestBody(
                        description = ApiConstants.REQUEST_BODY_DESCRIPTION,
                        required = true,
                        content = @Content(
                                schema = @Schema(implementation = BootcampDTO.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Ejemplo Bootcamp",
                                                value = ApiExamples.BOOTCAMP_DTO_JSON
                                        )
                                }
                        )
                ),
                responses = {
                        @ApiResponse(responseCode = ApiConstants.HTTP_CREATED, description = ApiConstants.RESPONSE_201),
                        @ApiResponse(responseCode = ApiConstants.HTTP_BAD_REQUEST, description = ApiConstants.RESPONSE_400),
                        @ApiResponse(responseCode = ApiConstants.HTTP_INTERNAL_ERROR, description = ApiConstants.RESPONSE_500)
                }
        )
)
public @interface BootcampApiDoc {}
