package co.com.prueba.entrypoints.handler;

import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import co.com.prueba.usecase.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private UserHandler userHandler;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .firstName("Daniel")
                .secondName("Ricardo")
                .firstLastName("Buritica")
                .secondLastName("Junco")
                .phone("3217570485")
                .address("Calle falsa 123")
                .residenceCity("Bogota")
                .build();
    }

    @Test
    void testFindByUsername_Success() {
        when(userUseCase.findUserByDocument(any(UserRequest.class)))
                .thenReturn(Mono.just(userResponse));

        MockServerRequest request = MockServerRequest.builder()
                .queryParam("documentType", "C")
                .queryParam("documentNumber", "23445322")
                .build();

        Mono<ServerResponse> result = userHandler.findByUsername(request);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertEquals(200, serverResponse.statusCode().value());
                    assertNotNull(serverResponse);
                })
                .verifyComplete();
    }

    @Test
    void testFindByUsername_EmptyParams() {
        when(userUseCase.findUserByDocument(any(UserRequest.class)))
                .thenReturn(Mono.just(userResponse));

        MockServerRequest request = MockServerRequest.builder()
                .build();

        Mono<ServerResponse> result = userHandler.findByUsername(request);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertEquals(200, serverResponse.statusCode().value());
                })
                .verifyComplete();
    }
}
