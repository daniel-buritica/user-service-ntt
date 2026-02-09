package co.com.prueba.adapter;

import co.com.prueba.model.exception.CustomException;
import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceAdapterTest {

    private UserServiceAdapter userServiceAdapter;

    @BeforeEach
    void setUp() {
        userServiceAdapter = new UserServiceAdapter();
    }

    @Test
    void testFindByUser_Success() {
        UserRequest request = UserRequest.builder()
                .documentType("C")
                .documentNumber("23445322")
                .build();

        Mono<UserResponse> result = userServiceAdapter.findByUser(request);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertNotNull(userResponse);
                    assertEquals("Daniel", userResponse.getFirstName());
                    assertEquals("Ricardo", userResponse.getSecondName());
                    assertEquals("Buritica", userResponse.getFirstLastName());
                    assertEquals("Junco", userResponse.getSecondLastName());
                    assertEquals("3217570485", userResponse.getPhone());
                    assertEquals("Calle falsa 123", userResponse.getAddress());
                    assertEquals("Bogota", userResponse.getResidenceCity());
                })
                .verifyComplete();
    }

    @Test
    void testFindByUser_NotFound() {
        UserRequest request = UserRequest.builder()
                .documentType("C")
                .documentNumber("99999999")
                .build();

        Mono<UserResponse> result = userServiceAdapter.findByUser(request);

        StepVerifier.create(result)
                .verifyErrorMatches(throwable -> {
                    assertTrue(throwable instanceof CustomException);
                    CustomException ex = (CustomException) throwable;
                    assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
                    assertEquals("No existe el usuario.", ex.getMessage());
                    return true;
                });
    }

    @Test
    void testFindByUser_WrongDocumentType() {
        UserRequest request = UserRequest.builder()
                .documentType("P")
                .documentNumber("23445322")
                .build();

        Mono<UserResponse> result = userServiceAdapter.findByUser(request);

        StepVerifier.create(result)
                .verifyErrorMatches(throwable -> {
                    assertTrue(throwable instanceof CustomException);
                    CustomException ex = (CustomException) throwable;
                    assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
                    return true;
                });
    }
}
