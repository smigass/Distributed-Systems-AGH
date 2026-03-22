package com.agh.student.smigas.radar.auth.opensky;

import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
public class TokenRequest{
    private String clientId;
    private String clientSecret;
    private String grantType;

    public MultiValueMap<String, String> toFormData() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", grantType);
        return formData;
    }
}
