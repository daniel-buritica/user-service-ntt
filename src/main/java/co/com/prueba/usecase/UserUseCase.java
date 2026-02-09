package co.com.prueba.usecase;

import co.com.prueba.model.exception.CustomException;
import co.com.prueba.model.user.DocumentType;
import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import co.com.prueba.model.user.gateway.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.Arrays;


@Log4j2
@RequiredArgsConstructor
public class UserUseCase {
    public static final String ERROR_MESSAGE_DOCUMENT_TYPE =
            "El valor del campo documentType`tiene que estar entre (C y P).";
    public static final String USER_SUCCESSFUL = "El usuario fue encontrado";
    private final UserRepository userRepository;
    public Mono<UserResponse> findUserByDocument(UserRequest request){
        return Mono.just(request)
                .filter(this::documentTypeValidate)
                .doOnError(throwable -> log.error(ERROR_MESSAGE_DOCUMENT_TYPE))
                .flatMap(userRepository::findByUser)
                .doOnNext(userResponse -> log.info(USER_SUCCESSFUL))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error(ERROR_MESSAGE_DOCUMENT_TYPE);
                    return Mono.error(new CustomException(HttpStatus.BAD_REQUEST,ERROR_MESSAGE_DOCUMENT_TYPE));
                }));


    }

    public boolean documentTypeValidate(UserRequest request) {
        var documentType = request.getDocumentType();
        return Arrays.stream(DocumentType.values())
                .map(Enum::name)
                .anyMatch(colorName -> colorName.equalsIgnoreCase(documentType));
    }

}