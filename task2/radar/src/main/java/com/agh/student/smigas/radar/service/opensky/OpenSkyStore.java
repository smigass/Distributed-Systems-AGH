package com.agh.student.smigas.radar.service.opensky;

import com.agh.student.smigas.radar.model.state.State;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenSkyStore {
    private final Map<String, State> stateCache = new HashMap<>();

    public void replaceCache(Map<String, State> newCache) {
        synchronized (stateCache) {
            stateCache.clear();
            stateCache.putAll(newCache);
        }
    }

    public List<State> getAllStates() {
        synchronized (stateCache) {
            return List.copyOf(stateCache.values());
        }
    }
}
