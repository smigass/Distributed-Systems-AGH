package com.agh.student.smigas.radar.service.aerodatabox;
import com.agh.student.smigas.radar.model.departure.Departure;
import com.agh.student.smigas.radar.model.flights.Airport;
import com.agh.student.smigas.radar.model.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AeroDataService {
    private final AeroDataStore store;
    private final AeroDataFetcher fetcher;
    private final AeroDataMapper mapper;

    public AeroDataService(AeroDataStore store, AeroDataFetcher fetcher, AeroDataMapper mapper) {
        this.store = store;
        this.fetcher = fetcher;
        this.mapper = mapper;
    }

    public ServerResponse<Airport> getAirportByICAO(String icao) {
        if (store.getAirport(icao) != null) {
            return ServerResponse.<Airport>builder().status(200)
                    .data(store.getAirport(icao)).build();
        }
        return fetcher.getAirportByIcao(icao);
    }

    public ServerResponse<List<Departure>> getDeparturesByLocalTime(String airportIcao, String fromLocal) {
        ServerResponse<List<Departure>> departures = fetcher.getDeparturesByLocalTime(airportIcao, fromLocal);

        if (departures.getStatus() != 200) {
            return ServerResponse.<List<Departure>>builder().status(departures.getStatus())
                    .message(departures.getMessage())
                    .build();
        }

        List<Departure> dep =  departures.getData().stream()
                .map(d -> mapper.addDepartureAirportToDeparture(d, airportIcao))
                .map(mapper::normalizeFlightNumber)
                .toList();

        return ServerResponse.<List<Departure>>builder().status(departures.getStatus())
                .data(dep)
                .message(departures.getMessage())
                .build();
    }

    public ServerResponse<List<Departure>> getDeparturesByLocalTimeAndDestination(String airportIcao, String fromLocal, String destinationIcao) {
        ServerResponse<List<Departure>> departures = fetcher.getDeparturesByLocalTime(airportIcao, fromLocal);
        if (departures.getStatus() == 200) {
            return ServerResponse.<List<Departure>>builder().status(departures.getStatus())
                    .data(departures.getData().stream()
                            .filter(d -> d.getMovement().getArrivalAirport() != null && destinationIcao.equalsIgnoreCase(d.getMovement().getArrivalAirport().getIcao()))
                            .map(d -> mapper.addDepartureAirportToDeparture(d, airportIcao))
                            .map(mapper::normalizeFlightNumber)
                            .toList())
                    .message(departures.getMessage())
                    .build();
        }
        return departures;
    }
}
