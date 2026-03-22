package com.agh.student.smigas.radar.service.aviationstack;

import com.agh.student.smigas.radar.model.response.ServerResponse;
import org.springframework.stereotype.Service;

@Service
public class AviationStackService {
    private final AviationStackFetcher fetcher;

    public AviationStackService(AviationStackFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public ServerResponse<String> getFlightIcao(String iata) {
        return fetcher.getFlightIcao(iata);
    }
}
