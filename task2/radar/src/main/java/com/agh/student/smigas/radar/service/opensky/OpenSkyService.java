package com.agh.student.smigas.radar.service.opensky;

import com.agh.student.smigas.radar.model.response.ServerResponse;
import com.agh.student.smigas.radar.model.state.State;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenSkyService {
    private final OpenSkyStore cacheStore;
    private final OpenSkyFetcher fetcher;

    public OpenSkyService(OpenSkyStore cacheStore, OpenSkyFetcher fetcher) {
        this.cacheStore = cacheStore;
        this.fetcher = fetcher;
    }

    public ServerResponse<List<State>> getFlights() {
        return ServerResponse.<List<State>>builder()
                .status(200)
                .data(cacheStore.getAllStates())
                .build();
    }

    public ServerResponse<List<State>> getFlightsBounded(double minLat, double maxLat, double minLon, double maxLon) {
        List<State> state = cacheStore.getAllStates().stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .filter(s -> s.getLatitude() >= minLat && s.getLatitude() <= maxLat)
                .filter(s -> s.getLongitude() >= minLon && s.getLongitude() <= maxLon)
                .toList();

        return ServerResponse.<List<State>>builder()
                .status(200)
                .data(state)
                .build();
    }

    public ServerResponse<State> getStateByCallsign(String flightCallsign) {
        fetcher.fetchAndStoreStats();

        State state = cacheStore.getAllStates().stream()
                .filter(s -> s.getIcao24() != null && s.getIcao24().trim().equalsIgnoreCase(flightCallsign.trim()))
                .findFirst()
                .orElse(null);

        if (state == null) {
            return ServerResponse.<State>builder()
                    .status(404)
                    .message("Flight with callsign " + flightCallsign + " not found")
                    .build();
        }
        return ServerResponse.<State>builder()
                .status(200)
                .data(state)
                .build();
    }
}
