package com.agh.student.smigas.radar.service.aviationstack;

import com.agh.student.smigas.radar.exceptions.ApiException;
import com.agh.student.smigas.radar.model.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;

@Slf4j
@Service
public class AviationStackFetcher {
    private final RestClient restClient;

    @Value("${aviationstack.api.key}")
    private String apiKey;

    public AviationStackFetcher(@Qualifier("aviationStackRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public ServerResponse<String> getFlightIcao(String iata) {
        try {
            JsonNode node = restClient.get()
                    .uri("https://api.aviationstack.com/v1/flights?flight_iata={iata}&access_key={apiKey}", iata, apiKey)
                    .retrieve()
                    .body(JsonNode.class);

            assert node != null;
            log.info(node.toPrettyString());

            String icao = node.path("data").get(0)
                    .path("aircraft")
                    .path("icao24")
                    .asString();

            return ServerResponse.<String>builder()
                    .status(200)
                    .data(icao)
                    .build();
        } catch (ApiException e) {
            return ServerResponse.<String>builder()
                    .status(e.getStatus())
                    .message("Failed to fetch flight data: " + e.getMessage())
                    .build();
        } catch (JsonNodeException e) {
            return ServerResponse.<String>builder()
                    .status(404)
                    .message("Can't track flight number: " + iata)
                    .build();
        }
    }
}

