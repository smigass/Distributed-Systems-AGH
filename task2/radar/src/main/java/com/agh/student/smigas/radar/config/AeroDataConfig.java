package com.agh.student.smigas.radar.config;

import com.agh.student.smigas.radar.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class AeroDataConfig {
    @Value("${aerobox.api.key}")
    private String apiKey;

    @Bean
    @Qualifier("aeroBoxRestClient")
    public RestClient aeroBoxRestClient() {
        return RestClient.builder()
                .defaultHeader("x-magicapi-key", apiKey)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    log.error("4xx error when calling AeroDataBox API: {} {}", response.getStatusCode(), response.getBody());
                    throw new ApiException(response.getStatusCode().value(), "Client error when calling AeroDataBox API");
                }))
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    log.error("5xx error when calling AeroDataBox API: {} {}", response.getStatusCode(), response.getBody());
                    throw new ApiException(response.getStatusCode().value(), "Server error when calling AeroDataBox API");
                }))
                .baseUrl("https://prod.api.market/api/v1/aedbx/aerodatabox")
                .build();
    }
}
