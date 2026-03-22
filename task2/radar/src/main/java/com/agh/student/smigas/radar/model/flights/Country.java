package com.agh.student.smigas.radar.model.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@AllArgsConstructor
@Getter
public class Country {
    private String code;
    private String name;
}
