package co.com.prueba;

import co.com.prueba.model.user.UserRequest;
import co.com.prueba.model.user.UserResponse;
import co.com.prueba.model.user.gateway.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    private UserResponse response;
    private UserRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.request = UserRequest.builder()
                .documentType("C")
                .documentNumber("23445322")
                .build();
        this.response = UserResponse.builder()
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
    public void testFindUserByDocumentOk() {
        when(userRepository.findByUser(request)).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("http://localhost:8090/api/v1/user?documentType=C&documentNumber=23445322")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(userResponse -> {
                    Assert.assertEquals("Daniel", userResponse.getFirstName());
                    Assert.assertEquals("Ricardo", userResponse.getSecondName());
                    Assert.assertEquals("Buritica", userResponse.getFirstLastName());
                    Assert.assertEquals("Junco", userResponse.getSecondLastName());
                    Assert.assertEquals("3217570485", userResponse.getPhone());
                    Assert.assertEquals("Calle falsa 123", userResponse.getAddress());
                    Assert.assertEquals("Bogota", userResponse.getResidenceCity());
                });
    }
    @Test
    public void testFindNotUserByNotDocument() {
        webTestClient.get()
                .uri("http://localhost:8090/api/v1/user?documentType=A&documentNumber=12345")
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    public void testFindUserByNotDocument() {
        webTestClient.get()
                .uri("http://localhost:8090/api/v1/user?documentType=A&documentNumber=2344532")
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    public void testFindNotUserByDocument() {
        webTestClient.get()
                .uri("http://localhost:8090/api/v1/user?documentType=C&documentNumber=12345")
                .exchange()
                .expectStatus().isNotFound();
    }
}
