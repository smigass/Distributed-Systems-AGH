package com.agh.student.smigas.radar.model.state;

import lombok.Data;

import java.util.List;

@Data
public class StateResponse {
    private long time;
    private List<List<Object>> states;
}
