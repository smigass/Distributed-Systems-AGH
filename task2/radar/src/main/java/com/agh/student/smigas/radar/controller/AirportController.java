package com.agh.student.smigas.radar.controller;

import com.agh.student.smigas.radar.model.departure.Departure;
import com.agh.student.smigas.radar.model.flights.Airport;
import com.agh.student.smigas.radar.model.response.ServerResponse;
import com.agh.student.smigas.radar.service.aerodatabox.AeroDataService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airport")
public class AirportController {
    private final AeroDataService aeroDataService;

    public AirportController(AeroDataService aeroDataService) {
        this.aeroDataService = aeroDataService;
    }

    @GetMapping
    @Operation(
            summary = "Get airport information",
            description = "Retrieves information about a specific airport, including its name, location, and other relevant details."
    )
    public ResponseEntity<ServerResponse<Airport>> getAirportByIcao(@RequestParam String icao){
        ServerResponse<Airport> airportServerResponse = aeroDataService.getAirportByICAO(icao.toUpperCase());
        if (airportServerResponse.getStatus() != 200) {
            ServerResponse<Airport> response = ServerResponse.<Airport>builder()
                    .status(airportServerResponse.getStatus())
                    .message("Airport not found: " + airportServerResponse.getMessage())
                    .build();
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        ServerResponse<Airport> response = ServerResponse.<Airport>builder()
                .status(200)
                .data(airportServerResponse.getData())
                .build();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @GetMapping("/departures")
    @Operation(
            summary = "Get airport departures",
            description = "Retrieves a list of departures from a specific airport within a specified local time."
    )
    public ResponseEntity<ServerResponse<List<Departure>>> getDepartures(
            @RequestParam String airportIcao,
            @RequestParam String fromLocal,
            @RequestParam(required = false) String destinationIcao
    ) {

        ServerResponse<List<Departure>> response = ServerResponse.<List<Departure>>builder()
                .status(200)
                .data(destinationIcao == null ?
                        aeroDataService.getDeparturesByLocalTime(airportIcao.toUpperCase(), fromLocal).getData() :
                        aeroDataService.getDeparturesByLocalTimeAndDestination(airportIcao.toUpperCase(), fromLocal, destinationIcao.toUpperCase()).getData())
                .build();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
