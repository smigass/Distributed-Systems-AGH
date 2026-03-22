package com.agh.student.smigas.radar.controller;

import com.agh.student.smigas.radar.model.response.ServerResponse;
import com.agh.student.smigas.radar.model.state.State;
import com.agh.student.smigas.radar.service.aviationstack.AviationStackService;
import com.agh.student.smigas.radar.service.opensky.OpenSkyService;
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

class FlightControllerTest {

    private OpenSkyService openSkyService;
    private AviationStackService aviationStackService;
    private FlightController controller;

    @BeforeEach
    void setUp() {
        openSkyService = mock(OpenSkyService.class);
        aviationStackService = mock(AviationStackService.class);
        controller = new FlightController(openSkyService, aviationStackService);
    }

    @Test
    void getFlightsShouldReturnStatusFromService() {
        ServerResponse<List<State>> serviceResponse = ServerResponse.<List<State>>builder()
                .status(200)
                .data(List.of(new State()))
                .build();
        when(openSkyService.getFlights()).thenReturn(serviceResponse);

        ResponseEntity<ServerResponse<List<State>>> response = controller.getFlights();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void getTrackedFlightShouldReturn404WhenIcaoCannotBeResolved() {
        when(aviationStackService.getFlightIcao("LO123")).thenReturn(ServerResponse.<String>builder()
                .status(200)
                .data(null)
                .build());

        ResponseEntity<ServerResponse<State>> response = controller.getTrackedFlight("LO123");

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("LO123"));
    }

    @Test
    void getTrackedFlightShouldQueryOpenSkyWhenIcaoExists() {
        State state = new State();
        state.setIcao24("abc123");

        when(aviationStackService.getFlightIcao("LO456")).thenReturn(ServerResponse.<String>builder()
                .status(200)
                .data("abc123")
                .build());
        when(openSkyService.getStateByCallsign("abc123")).thenReturn(ServerResponse.<State>builder()
                .status(200)
                .data(state)
                .build());

        ResponseEntity<ServerResponse<State>> response = controller.getTrackedFlight("LO456");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("abc123", response.getBody().getData().getIcao24());
        verify(openSkyService).getStateByCallsign("abc123");
    }
}

