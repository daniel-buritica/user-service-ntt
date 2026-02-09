package co.com.prueba.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigTest {

    @Test
    void testApplicationConfig_Beans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        // Verificar que los beans se crean correctamente
        WebProperties.Resources resources = context.getBean(WebProperties.Resources.class);
        assertNotNull(resources);

        OpenAPI openAPI = context.getBean(OpenAPI.class);
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("App API", openAPI.getInfo().getTitle());
        assertEquals("Documentación de la App", openAPI.getInfo().getDescription());

        context.close();
    }

    @Test
    void testResourcesBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        WebProperties.Resources resources = context.getBean(WebProperties.Resources.class);
        assertNotNull(resources);

        context.close();
    }

    @Test
    void testOpenAPIBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        OpenAPI openAPI = context.getBean(OpenAPI.class);
        assertNotNull(openAPI);
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getInfo());

        context.close();
    }
}
