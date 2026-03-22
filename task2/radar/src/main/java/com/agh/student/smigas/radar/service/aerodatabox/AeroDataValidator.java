package com.agh.student.smigas.radar.service.aerodatabox;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Component
public class AeroDataValidator {

    public boolean checkTime(String from) {
        if (!dateFormatIsValid(from)) {
            return false;
        }
        try {
            return true;
        } catch (DateTimeParseException e ){
          return false;
        }
    }

    public boolean checkIcaoFormat(String icao) {
        if (icao == null) {
            return false;
        }
        return icao.matches("[A-Z]{4}");
    }

    private boolean dateFormatIsValid(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2})?");
    }

    public String dateDeltaHours(String time) {
        LocalDateTime dateTime = LocalDateTime.parse(time);
        return dateTime.plusHours(11).plusMinutes(59).toString();
    }
}
