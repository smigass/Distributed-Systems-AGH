package com.agh.student.smigas.radar.model.departure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Movement {
    private AirportData departureAirport;
    @JsonProperty("airport")
    private AirportData arrivalAirport;
    private Time scheduledTime;
    private Time revisedTime;
}
