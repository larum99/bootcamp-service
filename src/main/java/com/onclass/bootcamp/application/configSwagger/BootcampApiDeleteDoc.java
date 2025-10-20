package com.onclass.bootcamp.application.configSwagger;

import com.onclass.bootcamp.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RouterOperation(
        path = Constants.BOOTCAMP_PATH + "/{id}",
        beanClass = BootcampHandlerImpl.class,
        beanMethod = "deleteBootcamp",
        operation = @Operation(
                operationId = "deleteBootcamp",
                summary = "Eliminar un bootcamp",
                description = "Elimina un bootcamp por su identificador. La operación también elimina sus capacidades y tecnologías asociadas si no están relacionadas con otros bootcamps.",
                parameters = {
                        @Parameter(
                                name = ApiConstants.HEADER_X_MESSAGE_ID,
                                in = ParameterIn.HEADER,
                                description = ApiConstants.HEADER_X_MESSAGE_ID_DESC,
                                required = true
                        ),
                        @Parameter(
                                name = "id",
                                in = ParameterIn.PATH,
                                description = "ID del bootcamp que se desea eliminar",
                                required = true
                        )
                },
                responses = {
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_NO_CONTENT,
                                description = "Bootcamp eliminado exitosamente"
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_BAD_REQUEST,
                                description = ApiConstants.RESPONSE_400
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_NOT_FOUND,
                                description = "No se encontró el bootcamp con el ID especificado"
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_INTERNAL_ERROR,
                                description = ApiConstants.RESPONSE_500
                        )
                }
        )
)
public @interface BootcampApiDeleteDoc {}
