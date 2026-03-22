package com.agh.student.smigas.radar.controller;

import com.agh.student.smigas.radar.model.response.ServerResponse;
import com.agh.student.smigas.radar.model.state.State;
import com.agh.student.smigas.radar.service.aviationstack.AviationStackService;
import com.agh.student.smigas.radar.service.opensky.OpenSkyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:*")
public class FlightController {
    private final OpenSkyService openSkyService;
    private final AviationStackService aviationStackService;

    public FlightController(OpenSkyService openSkyService, AviationStackService aviationStackService) {
        this.openSkyService = openSkyService;
        this.aviationStackService = aviationStackService;
    }

    @GetMapping("/flights")
    @Operation(
            summary = "Get all flights",
            description = "Retrieves a list of all flights currently tracked by the OpenSky Network."
    )
    public ResponseEntity<ServerResponse<List<State>>> getFlights() {
        ServerResponse<List<State>> response = openSkyService.getFlights();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/flights/bounded")
    @Operation(
            summary = "Get flights bounded by lat and lon",
            description = "Retrieves a list of flights currently tracked by the OpenSky Network that are within the specified latitude and longitude bounds."
    )
    public ResponseEntity<ServerResponse<List<State>>> getBoundedFlights(
            @RequestParam double minLat,
            @RequestParam double maxLat,
            @RequestParam double minLon,
            @RequestParam double maxLon
    ) {
        ServerResponse<List<State>> response = openSkyService.getFlightsBounded(minLat, maxLat, minLon, maxLon);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/flights/icao")
    @Operation(
            summary = "Get flight ICAO24 code",
            description = "Retrieves the ICAO24 code for a specific flight based on its IATA code."
    )
    public ResponseEntity<ServerResponse<String>> getFlightIcao(@RequestParam String flightIata) {
        ServerResponse<String> response = aviationStackService.getFlightIcao(flightIata);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/flights/track")
    @Operation(
            summary = "Get tracked flight state by IATA code",
            description = "Retrieves the current state of a specific flight being tracked by the OpenSky Network based on its IATA code."
    )
    public ResponseEntity<ServerResponse<State>> getTrackedFlight(@RequestParam String flightIata) {
        String icao = aviationStackService.getFlightIcao(flightIata).getData();

        if (icao == null) {
            ServerResponse<State> response = ServerResponse.<State>builder()
                    .status(404)
                    .message("Flight with IATA code " + flightIata + " can not be tracked")
                    .build();
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        ServerResponse<State> response = openSkyService.getStateByCallsign(icao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
