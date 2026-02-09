package co.com.prueba.model.user.gateway;

import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<UserResponse> findByUser(UserRequest request);

}
