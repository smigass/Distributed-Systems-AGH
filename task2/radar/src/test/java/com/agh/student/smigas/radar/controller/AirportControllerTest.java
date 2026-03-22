package com.agh.student.smigas.radar.controller;

import com.agh.student.smigas.radar.model.departure.Departure;
import com.agh.student.smigas.radar.model.flights.Airport;
import com.agh.student.smigas.radar.model.response.ServerResponse;
import com.agh.student.smigas.radar.service.aerodatabox.AeroDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AirportControllerTest {

    private AeroDataService aeroDataService;
    private AirportController controller;

    @BeforeEach
    void setUp() {
        aeroDataService = mock(AeroDataService.class);
        controller = new AirportController(aeroDataService);
    }

    @Test
    void getAirportByIcaoShouldReturnNotFoundWhenServiceFails() {
        when(aeroDataService.getAirportByICAO("XXXX")).thenReturn(ServerResponse.<Airport>builder()
                .status(404)
                .message("no data")
                .build());

        ResponseEntity<ServerResponse<Airport>> response = controller.getAirportByIcao("xxxx");

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Airport not found"));
    }

    @Test
    void getAirportByIcaoShouldReturnAirportWhenFound() {
        Airport airport = Airport.builder()
                .icao("EPKK")
                .fullName("Krakow Airport")
                .build();

        when(aeroDataService.getAirportByICAO("EPKK")).thenReturn(ServerResponse.<Airport>builder()
                .status(200)
                .data(airport)
                .build());

        ResponseEntity<ServerResponse<Airport>> response = controller.getAirportByIcao("epkk");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("EPKK", response.getBody().getData().getIcao());
    }

    @Test
    void getDeparturesShouldUseUppercasedAirportIcao() {
        Departure departure = Departure.builder()
                .number("LO391")
                .build();

        when(aeroDataService.getDeparturesByLocalTime("EPKK", "2026-03-22T10:00")).thenReturn(ServerResponse.<List<Departure>>builder()
                .status(200)
                .data(List.of(departure))
                .build());

        ResponseEntity<ServerResponse<List<Departure>>> response = controller.getDepartures("epkk", "2026-03-22T10:00", null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(aeroDataService).getDeparturesByLocalTime("EPKK", "2026-03-22T10:00");
    }
}

