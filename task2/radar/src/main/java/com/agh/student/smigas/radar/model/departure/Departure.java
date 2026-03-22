package com.agh.student.smigas.radar.model.departure;

import com.agh.student.smigas.radar.model.flights.Airline;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
@ToString
@Setter
public class Departure {
    private Movement movement;
    private String number;
    private String status;
    private Boolean isCargo;
    private Aircraft aircraft;
    private Airline airline;
}

