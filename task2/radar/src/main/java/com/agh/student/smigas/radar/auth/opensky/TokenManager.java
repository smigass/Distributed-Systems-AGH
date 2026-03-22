package com.agh.student.smigas.radar.auth.opensky;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Component
@ConditionalOnProperty(name = "opensky.api.token-management", havingValue = "custom")
public class TokenManager {
    @Value("${opensky.api.client-id}")
    private String clientId;

    @Value("${opensky.api.client-secret}")
    private String clientSecret;

    @Value("${opensky.api.token-margin}")
    private int tokenMargin;

    @Value("${opensky.api.token-url}")
    private String tokenUrl;

    private LocalDateTime expiresAt;
    private String token;

    public String getToken() {
        if (token != null && !isTokenExpired()) {
            return token;
        }
        return fetchToken();
    }

    private String fetchToken() {
        RestClient restClient = RestClient.builder().build();
        TokenRequest tokenRequest = TokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
        ResponseEntity<TokenResponse> res =  restClient.post()
                .uri(tokenUrl)
                .body(tokenRequest.toFormData())
                .contentType(APPLICATION_FORM_URLENCODED)
                .retrieve()
                .toEntity(TokenResponse.class);
        if (res.getBody() != null) {
            this.token = res.getBody().getAccessToken();
        }
        this.expiresAt = LocalDateTime.now().plusSeconds(res.getBody().getExpiresIn());
        return token;
    }

    private boolean isTokenExpired() {
       return expiresAt == null || LocalDateTime.now().isAfter(expiresAt.minusSeconds(tokenMargin));
    }

    @PostConstruct
    public void init() {
        log.info("Initializing TokenManager");
    }
}
