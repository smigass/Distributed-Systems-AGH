package com.agh.student.smigas.radar.service.aerodatabox;

import com.agh.student.smigas.radar.model.flights.Airport;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AeroDataStore {
    private final Map<String, Airport> airportsCache = new HashMap<>();

    public synchronized Airport getAirport(String icao) {
        return airportsCache.get(icao);
    }

    public synchronized void addAirport(Airport airport) {
        airportsCache.put(airport.getIcao(), airport);
    }

}
