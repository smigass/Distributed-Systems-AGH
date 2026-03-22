package com.agh.student.smigas.radar.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> {
    private T data;
    private String message;
    private int status;
}
