package com.agh.student.smigas.radar.model.departure;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
@ToString
public class AirportData {
    private String icao;
    private String iata;
    private String name;
    private String timeZone;
    private String countryCode;
}
