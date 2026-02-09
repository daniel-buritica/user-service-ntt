package co.com.prueba.model.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomAttributeTest {

    private CustomAttribute customAttribute;

    @BeforeEach
    void setUp() {
        customAttribute = new CustomAttribute();
    }

    @Test
    void testCustomAttribute_Initialization() {
        assertNotNull(customAttribute);
    }

    @Test
    void testCustomAttribute_ExtendsDefaultErrorAttributes() {
        // Verificar que CustomAttribute es una instancia de DefaultErrorAttributes
        assertTrue(customAttribute instanceof org.springframework.boot.web.reactive.error.DefaultErrorAttributes);
    }
}
