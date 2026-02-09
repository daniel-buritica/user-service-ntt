package co.com.prueba.usecase;

import co.com.prueba.model.user.DocumentType;
import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import co.com.prueba.model.exception.CustomException;
import co.com.prueba.model.user.gateway.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @InjectMocks
    private UserUseCase userUseCase;
    @Mock
    private UserRepository userRepository;
    @Test
    public void testDocumentTypeValidateOk() {
        UserRequest requestValid = UserRequest.builder()
                .documentType(DocumentType.C.name())
                .documentNumber("23445322")
                .build();
        boolean isValid = userUseCase.documentTypeValidate(requestValid);
        assert (isValid);
    }

    @Test
    public void testDocumentTypeValidateError() {
        UserRequest requestInvalid = UserRequest.builder()
                .documentType("X")
                .documentNumber("23445322")
                .build();

        boolean isInvalid = userUseCase.documentTypeValidate(requestInvalid);
        assert (!isInvalid);
    }
    @Test
    public void testFindUserByDocumentOk() {
        UserRequest request = UserRequest.builder()
                .documentType("C")
                .documentNumber("23445322")
                .build();

        UserResponse response = UserResponse.builder()
                .firstName("Daniel")
                .secondName("Ricardo")
                .firstLastName("Buritica")
                .secondLastName("Junco")
                .phone("3217570485")
                .address("Calle falsa 123")
                .residenceCity("Bogota")
                .build();

        when(userRepository.findByUser(request)).thenReturn(Mono.just(response));

        userUseCase.findUserByDocument(request)
                .as(StepVerifier::create)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void testFindUserByDocumentError() {
        UserRequest request = UserRequest.builder()
                .documentType("Z")
                .documentNumber("23445322")
                .build();

        Mono<UserResponse> result = userUseCase.findUserByDocument(request);

        StepVerifier.create(result)
                .verifyErrorMatches(ex -> ex instanceof CustomException && ((CustomException) ex).getStatus() == HttpStatus.BAD_REQUEST);
    }

}