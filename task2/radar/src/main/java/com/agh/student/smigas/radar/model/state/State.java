package com.agh.student.smigas.radar.model.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@JsonPropertyOrder({
        "icao24",
        "callsign",
        "origin_country",
        "time_position",
        "last_contact",
        "longitude",
        "latitude",
        "baro_altitude",
        "on_ground",
        "velocity",
        "true_track",
        "vertical_rate",
        "geo_altitude",
        "squawk"})
public class State {
    @JsonProperty("icao24")
    private String icao24;

    @JsonProperty("callsign")
    private String callsign;

    @JsonProperty("origin_country")
    private String originCountry;

    @JsonProperty("time_position")
    private Integer timePosition;

    @JsonProperty("last_contact")
    private Integer lastContact;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty(value = "baro_altitude", defaultValue = "0.0")
    private Double baroAltitude;

    @JsonProperty("on_ground")
    private Boolean onGround;

    @JsonProperty("velocity")
    private Double velocity;

    @JsonProperty("true_track")
    private Double trueTrack;

    @JsonProperty("vertical_rate")
    private Double verticalRate;

    @JsonProperty("geo_altitude")
    private Double geoAltitude;

    @JsonProperty("squawk")
    private String squawk;
}
