package co.com.prueba.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"co.com.prueba.usecase","co.com.prueba.adapter","co.com.prueba.model"},
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+Adapter$"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+Repository$"),
        },
        useDefaultFilters = false)
public class ApplicationConfig {

        @Bean
        public WebProperties.Resources resources(){
                return new WebProperties.Resources();
        }

        @Bean
        public OpenAPI reactiveOpenAPI() {
                return new OpenAPI()
                        .components(new Components())
                        .info(new Info().title("App API").description("Documentación de la App"));
        }

}
