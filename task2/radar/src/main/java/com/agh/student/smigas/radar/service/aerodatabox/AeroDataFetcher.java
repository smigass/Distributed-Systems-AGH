package com.agh.student.smigas.radar.service.aerodatabox;

import com.agh.student.smigas.radar.exceptions.ApiException;
import com.agh.student.smigas.radar.model.departure.Departure;
import com.agh.student.smigas.radar.model.flights.Airport;
import com.agh.student.smigas.radar.model.response.DepartureResponse;
import com.agh.student.smigas.radar.model.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class AeroDataFetcher {
    private final AeroDataStore store;
    private final RestClient restClient;
    private final AeroDataValidator validator;


    public AeroDataFetcher(AeroDataStore store, @Qualifier("aeroBoxRestClient") RestClient restClient, AeroDataValidator validator) {
        this.store = store;
        this.restClient = restClient;
        this.validator = validator;
    }

    public ServerResponse<Airport> getAirportByIcao(String icao) {
        if (store.getAirport(icao) != null) {
            log.info("Airport data for ICAO: {} fetched from cache", icao);

            return ServerResponse.<Airport>builder().status(HttpStatus.OK.value())
                    .data(store.getAirport(icao)).build();
        }
        try {
            ResponseEntity<Airport> response = restClient.get()
                    .uri("/airports/Icao/" + icao + "?withTime=true")
                    .retrieve()
                    .toEntity(Airport.class);
            store.addAirport(response.getBody());

            return ServerResponse.<Airport>builder().status(response.getStatusCode().value())
                    .data(response.getBody()).build();

        } catch (ApiException e) {
            log.error("Error fetching airport data for ICAO: {}. Status code: {}, Message: {}", icao, e.getStatus(), e.getMessage());
            return ServerResponse.<Airport>builder().status(e.getStatus())
                    .message("Failed to fetch airport data: " + e.getMessage()).build();
        }
    }

    public ServerResponse<List<Departure>> getDeparturesByLocalTime(String airportIcao, String fromLocal) {
        if (!validator.checkTime(fromLocal)) {
            return ServerResponse.<List<Departure>>builder().status(HttpStatus.BAD_REQUEST.value())
                    .message("Invalid time").build();
        }
        try {
            String toLocal = validator.dateDeltaHours(fromLocal);

            ResponseEntity<DepartureResponse> departures = restClient.get()
                    .uri("/flights/airports/Icao/{icao}/{fromLocal}/{toLocal}", airportIcao, fromLocal, toLocal)
                    .retrieve()
                    .toEntity(DepartureResponse.class);

            if (departures.getBody() == null) {
                log.warn("No departure data found for airport ICAO: {} in the specified time range", airportIcao);
                return ServerResponse.<List<Departure>>builder().status(HttpStatus.NO_CONTENT.value())
                        .message("No departure data found").build();
            }


            return ServerResponse.<List<Departure>>builder().status(departures.getStatusCode().value())
                    .data(departures.getBody().getDepartures()).build();

        } catch (ApiException e) {
            log.error("Error fetching departures for airport ICAO: {}. Status code: {}, Message: {}", airportIcao, e.getStatus(), e.getMessage());
            return ServerResponse.<List<Departure>>builder().status(e.getStatus())
                    .message("Failed to fetch departures: " + e.getMessage()).build();
        }
    }



}
