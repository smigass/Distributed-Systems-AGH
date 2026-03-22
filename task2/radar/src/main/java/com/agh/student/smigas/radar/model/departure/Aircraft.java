package com.agh.student.smigas.radar.model.departure;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
public class Aircraft {
    private String model;
}
