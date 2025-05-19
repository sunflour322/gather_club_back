package com.gather_club_back.gather_club_back.yandex_disk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {
    @Value("${yandex.disk.access-token}")
    private String accessToken;

    @Bean
    public RestTemplate yandexDiskRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "OAuth " + accessToken);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
