package com.onclass.bootcamp.infrastructure.entrypoints;

import com.onclass.bootcamp.application.configSwagger.BootcampApiDoc;
import com.onclass.bootcamp.application.configSwagger.BootcampGetApiDoc;
import com.onclass.bootcamp.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import com.onclass.bootcamp.infrastructure.entrypoints.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @BootcampApiDoc
    public RouterFunction<ServerResponse> createBootcampRoute(BootcampHandlerImpl handler) {
        return route(POST(Constants.BOOTCAMP_PATH), handler::createBootcamp);
    }

    @Bean
    @BootcampGetApiDoc
    public RouterFunction<ServerResponse> getBootcampsRoute(BootcampHandlerImpl handler) {
        return route(GET(Constants.BOOTCAMP_PATH), handler::getBootcamps);
    }

    @Bean
    //@BootcampApiDoc
    public RouterFunction<ServerResponse> deleteBootcampRoute(BootcampHandlerImpl handler) {
        return route(DELETE(Constants.BOOTCAMP_PATH + "/{id}"), handler::deleteBootcamp);
    }
}
