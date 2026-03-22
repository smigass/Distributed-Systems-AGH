package com.agh.student.smigas.radar.model.response;

import com.agh.student.smigas.radar.model.departure.Departure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
public class DepartureResponse {
    private List<Departure> departures;
}
