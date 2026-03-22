package com.agh.student.smigas.radar.service.aerodatabox;

import com.agh.student.smigas.radar.model.departure.AirportData;
import com.agh.student.smigas.radar.model.departure.Departure;
import com.agh.student.smigas.radar.model.flights.Airport;
import org.springframework.stereotype.Service;

@Service
public class AeroDataMapper {
    private final AeroDataStore store;
    private final AeroDataFetcher fetcher;

    public AeroDataMapper(AeroDataStore store, AeroDataFetcher fetcher) {
        this.store = store;
        this.fetcher = fetcher;
    }

    public Departure normalizeFlightNumber(Departure departure) {
        String number = departure.getNumber().replaceAll("\\s+", "");
        departure.setNumber(number);
        return departure;
    }

    public Departure addDepartureAirportToDeparture(Departure departure, String departureIcao) {
        Airport airport = store.getAirport(departureIcao);
        if (airport == null) {
            airport = fetcher.getAirportByIcao(departureIcao).getData();
        }
        AirportData data = AirportData.builder()
                .icao(airport.getIcao())
                .iata(airport.getIata())
                .name(airport.getMunicipalityName())
                .timeZone(airport.getTimeZone())
                .countryCode(airport.getCountry().getCode())
                .build();
        departure.getMovement().setDepartureAirport(data);
        return departure;
    }
}
