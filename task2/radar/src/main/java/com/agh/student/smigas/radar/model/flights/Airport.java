package com.agh.student.smigas.radar.model.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
@ToString
public class Airport {
    private String icao;
    private String iata;
    private String fullName;
    private String municipalityName;
    private String timeZone;
    private Country country;
}
