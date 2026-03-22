package com.agh.student.smigas.radar.model.departure;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
@ToString
public class Time {
    private String utc;
    private String local;
}
