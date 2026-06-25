package com.dataforge.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DataForge TR - Enterprise Mock Data API")
                        .version("1.0.0")
                        .description("BDDK uyumlu asenkron sentetik müşteri, hesap ve işlem üretim motoru.")
                        .contact(new Contact()
                                .name("Muhammed Ali Işık")
                                .email("isik.mali@outlook.com")));
    }
}