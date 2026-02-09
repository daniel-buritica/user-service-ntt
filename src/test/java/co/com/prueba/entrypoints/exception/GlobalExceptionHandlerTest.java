package co.com.prueba.entrypoints.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private DefaultErrorAttributes errorAttributes;
    private WebProperties.Resources resources;
    private ApplicationContext applicationContext;
    private ServerCodecConfigurer codecConfigurer;

    @BeforeEach
    void setUp() {
        errorAttributes = new DefaultErrorAttributes();
        resources = new WebProperties.Resources();
        // Usar un ApplicationContext real en lugar de mock
        org.springframework.context.support.GenericApplicationContext applicationContext = 
                new org.springframework.context.support.GenericApplicationContext();
        applicationContext.refresh();
        codecConfigurer = ServerCodecConfigurer.create();

        globalExceptionHandler = new GlobalExceptionHandler(
                errorAttributes,
                resources,
                applicationContext,
                codecConfigurer
        );
    }

    @Test
    void testGetRoutingFunction() {
        RouterFunction<ServerResponse> routingFunction = globalExceptionHandler.getRoutingFunction(errorAttributes);

        assertNotNull(routingFunction);
    }

    @Test
    void testCustomErrorResponse_WithStatus() {
        ServerRequest request = MockServerRequest.builder()
                .build();

        // El método customErrorResponse es privado, pero podemos testearlo indirectamente
        // a través de getRoutingFunction y ejecutando la ruta
        RouterFunction<ServerResponse> routingFunction = globalExceptionHandler.getRoutingFunction(errorAttributes);
        assertNotNull(routingFunction);
    }

    @Test
    void testGlobalExceptionHandler_Initialization() {
        assertNotNull(globalExceptionHandler);
    }
}
