package com.agh.student.smigas.radar.service.opensky;

import com.agh.student.smigas.radar.model.state.State;
import com.agh.student.smigas.radar.model.state.StateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableScheduling
public class OpenSkyFetcher {
    private final OpenSkyStore store;
    private final RestClient restClient;
    private final OpenSkyMapper mapper;

    public OpenSkyFetcher(OpenSkyStore store, @Qualifier("oauthRestClient") RestClient restClient, OpenSkyMapper mapper) {
        this.store = store;
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Scheduled(fixedRate = 600000)
    public void fetchAndStoreStats() {
        log.info("Fetching flight states from OpenSky API...");

        StateResponse response = restClient.get()
                .uri("https://opensky-network.org/api/states/all")
                .retrieve()
                .body(StateResponse.class);

        if (response == null) {
            log.error("Failed to fetch data from OpenSky API");
            return;
        }
        List<State> states = mapper.toStates(response.getStates());
        store.replaceCache(
                states.stream().collect(Collectors.toMap(State::getIcao24, s -> s))
        );
        log.info("Fetched flight states form OpenSky API");
    }}
