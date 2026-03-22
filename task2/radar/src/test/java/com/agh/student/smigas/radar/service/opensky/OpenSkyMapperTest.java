package com.agh.student.smigas.radar.service.opensky;

import com.agh.student.smigas.radar.model.state.State;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OpenSkyMapperTest {

    private final OpenSkyMapper mapper = new OpenSkyMapper();

    @Test
    void toStateShouldMapNumbersAndIgnoreUnparseableValues() {
        List<Object> row = List.of(
                "abc123",
                "LOT391",
                "Poland",
                "n/a",
                1700000,
                "bad",
                50.06,
                12000.5,
                false,
                230,
                95.3,
                "oops",
                "ignored",
                12340.0,
                "1234"
        );

        State state = mapper.toState(row);

        assertEquals("abc123", state.getIcao24());
        assertEquals(1700000, state.getLastContact());
        assertNull(state.getTimePosition());
        assertNull(state.getLongitude());
        assertEquals(50.06, state.getLatitude());
        assertNull(state.getVerticalRate());
        assertEquals(230.0, state.getVelocity());
    }
}

