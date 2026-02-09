package co.com.prueba.entrypoints.router;

import co.com.prueba.entrypoints.handler.UserHandler;
import co.com.prueba.model.user.CustomExceptionE;
import co.com.prueba.model.user.UserResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Component
@Configuration
public class UserRouter {


    public static final String MESSAGE_200 = "Entrega la información del usuario";
    public static final String MESSAGE_404 = "No existe el usuario";

    @Bean
    public RouterFunction<ServerResponse> routerFunctionUser(UserHandler handler) {
        return route().GET("/api/v1/user", handler::findByUsername, ops -> ops
                .operationId("hello")
                .parameter(parameterBuilder().name("documentType").description("Tipo de documento [P - C]"))
                .parameter(parameterBuilder().name("documentNumber").description("número de documento"))
                .response(responseBuilder().description(MESSAGE_200).responseCode(HttpStatus.OK.toString())
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(UserResponse.class))))
                .response(responseBuilder().description(MESSAGE_404).responseCode(HttpStatus.NOT_FOUND.toString())
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomExceptionE.class))))
        ).build();
    }



}
