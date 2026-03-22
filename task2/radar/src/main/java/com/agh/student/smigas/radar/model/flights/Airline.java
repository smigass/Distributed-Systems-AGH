package com.agh.student.smigas.radar.model.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
public class Airline {
    String name;
    String iata;
    String icao;
}
