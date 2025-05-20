package com.gather_club_back.gather_club_back.yandex_disk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {
    @Value("${yandex.disk.access-token}")
    private String accessToken;

    @Bean
    public RestTemplate yandexDiskRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Удаляем использование HttpComponentsClientHttpRequestFactory
        // и используем стандартный SimpleClientHttpRequestFactory
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "OAuth " + accessToken);
            request.getHeaders().add("Accept", "application/json");
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}