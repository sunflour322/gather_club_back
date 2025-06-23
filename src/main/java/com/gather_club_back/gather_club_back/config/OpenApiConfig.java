package com.gather_club_back.gather_club_back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatherClubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gather Club API")
                        .description("API для мобильного приложения Gather Club")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gather Club")
                                .email("support@gatherclub.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("/").description("Локальный сервер"),
                        new Server().url("http://212.67.8.92:8080/").description("Продакшн сервер")
                ))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("Введите JWT токен в формате: Bearer {token}")
                        ))
                .addSecurityItem(new SecurityRequirement().addList("JWT"));
    }
}
