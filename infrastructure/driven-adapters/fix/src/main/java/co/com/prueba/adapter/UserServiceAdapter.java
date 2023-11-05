package co.com.prueba.adapter;

import co.com.prueba.model.exception.CustomException;
import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import co.com.prueba.model.user.gateway.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Log4j2
public class UserServiceAdapter implements UserRepository {

    public static final String USER = "23445322";
    public static final String CEDULA_CIUDADANIA = "C";
    public static final String SEARCH_SUCCESSFUL = "El usuario fue encontrado";
    public static final String USER_NOT_FOUND = "No existe el usuario.";

    @Override
    public Mono<UserResponse> findByUser(UserRequest request) {
        return Mono.just(request)
                .filter(this::validateIfUserExists)
                .flatMap(t -> Mono.just(getUserResponse()))
                .doOnNext(userResponse -> log.info(SEARCH_SUCCESSFUL))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, USER_NOT_FOUND)))
                .doOnError(throwable -> log.error(USER_NOT_FOUND));

    }

    private boolean validateIfUserExists(UserRequest request) {
        return request.getDocumentNumber().equals(USER) && request.getDocumentType().equals(CEDULA_CIUDADANIA);
    }

    private UserResponse getUserResponse() {
        return UserResponse
                .builder()
                .firstName("Daniel")
                .secondName("Ricardo")
                .firstLastName("Buritica")
                .secondLastName("Junco")
                .phone("3217570485")
                .address("Calle falsa 123")
                .residenceCity("Bogota")
                .build();
    }
}
