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
        path = Constants.BOOTCAMP_PATH + ApiExamples.PATH_ID_SEGMENT,
        beanClass = BootcampHandlerImpl.class,
        beanMethod = ApiExamples.DELETE_BOOTCAMP_METHOD,
        operation = @Operation(
                operationId = ApiExamples.DELETE_BOOTCAMP_OPERATION_ID,
                summary = ApiExamples.DELETE_BOOTCAMP_SUMMARY,
                description = ApiExamples.DELETE_BOOTCAMP_DESCRIPTION,
                parameters = {
                        @Parameter(
                                name = ApiConstants.HEADER_X_MESSAGE_ID,
                                in = ParameterIn.HEADER,
                                description = ApiConstants.HEADER_X_MESSAGE_ID_DESC,
                                required = true
                        ),
                        @Parameter(
                                name = ApiExamples.ID_PARAM_NAME,
                                in = ParameterIn.PATH,
                                description = ApiExamples.ID_PARAM_DESCRIPTION,
                                required = true
                        )
                },
                responses = {
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_NO_CONTENT,
                                description = ApiExamples.DELETE_SUCCESS_DESCRIPTION
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_BAD_REQUEST,
                                description = ApiConstants.RESPONSE_400
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_NOT_FOUND,
                                description = ApiExamples.NOT_FOUND_DESCRIPTION
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_INTERNAL_ERROR,
                                description = ApiConstants.RESPONSE_500
                        )
                }
        )
)
public @interface BootcampApiDeleteDoc {}
