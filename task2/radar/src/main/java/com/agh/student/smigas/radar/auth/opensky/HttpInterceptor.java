package com.agh.student.smigas.radar.auth.opensky;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HttpInterceptor implements ClientHttpRequestInterceptor {
    private final TokenManager tokenManager;

    public HttpInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte @NonNull [] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setBearerAuth(tokenManager.getToken());
        return execution.execute(request, body);
    }
}
