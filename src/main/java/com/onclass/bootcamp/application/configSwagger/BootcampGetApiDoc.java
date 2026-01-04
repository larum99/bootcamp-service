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
        path = Constants.BOOTCAMP_PATH,
        beanClass = BootcampHandlerImpl.class,
        beanMethod = ApiExamples.GET_BOOTCAMPS_METHOD,
        operation = @Operation(
                operationId = ApiExamples.GET_BOOTCAMPS_OPERATION_ID,
                summary = ApiExamples.GET_BOOTCAMPS_SUMMARY,
                description = ApiExamples.GET_BOOTCAMPS_DESCRIPTION,
                parameters = {
                        @Parameter(
                                name = ApiConstants.HEADER_X_MESSAGE_ID,
                                in = ParameterIn.HEADER,
                                description = ApiConstants.HEADER_X_MESSAGE_ID_DESC,
                                required = true
                        ),
                        @Parameter(
                                name = ApiConstants.PARAM_PAGE,
                                in = ParameterIn.QUERY,
                                description = ApiConstants.PARAM_PAGE_DESC
                        ),
                        @Parameter(
                                name = ApiConstants.PARAM_SIZE,
                                in = ParameterIn.QUERY,
                                description = ApiConstants.PARAM_SIZE_DESC
                        ),
                        @Parameter(
                                name = ApiConstants.PARAM_SORT_BY,
                                in = ParameterIn.QUERY,
                                description = ApiConstants.PARAM_SORT_BY_DESC
                        ),
                        @Parameter(
                                name = ApiConstants.PARAM_SORT_ORDER,
                                in = ParameterIn.QUERY,
                                description = ApiConstants.PARAM_SORT_ORDER_DESC
                        )
                },
                responses = {
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_OK,
                                description = ApiConstants.RESPONSE_200
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_BAD_REQUEST,
                                description = ApiConstants.RESPONSE_400
                        ),
                        @ApiResponse(
                                responseCode = ApiConstants.HTTP_INTERNAL_ERROR,
                                description = ApiConstants.RESPONSE_500
                        )
                }
        )
)
public @interface BootcampGetApiDoc {}
