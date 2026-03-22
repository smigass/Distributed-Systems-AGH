package com.agh.student.smigas.radar.service.opensky;

import com.agh.student.smigas.radar.model.state.State;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenSkyMapper {
    public List<State> toStates(List<List<Object>> rows) {
        List<State> states = new ArrayList<>();
        for (List<Object> row : rows) {
            State mapped = toState(row);
            if (mapped != null) {
                states.add(mapped);
            }
        }
        return states;
    }

    public State toState(List<Object> row) {
        State state = new State();
        state.setIcao24((String) row.getFirst());
        state.setCallsign((String) row.get(1));
        state.setOriginCountry((String) row.get(2));
        state.setTimePosition(toIntegerSafe(row.get(3)));
        state.setLastContact(toIntegerSafe(row.get(4)));
        state.setLongitude(toDoubleSafe(row.get(5)));
        state.setLatitude(toDoubleSafe(row.get(6)));
        state.setBaroAltitude(toDoubleSafe(row.get(7)));
        state.setOnGround((Boolean) row.get(8));
        state.setVelocity(toDoubleSafe(row.get(9)));
        state.setTrueTrack(toDoubleSafe(row.get(10)));
        state.setVerticalRate(toDoubleSafe(row.get(11)));
        state.setGeoAltitude(toDoubleSafe(row.get(13)));
        state.setSquawk((String) row.get(14));
        return state;
    }

    private Integer toIntegerSafe(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        return null;
    }

    private Double toDoubleSafe(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }
}

