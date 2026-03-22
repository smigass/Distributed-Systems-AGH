package com.agh.student.smigas.radar.service.aerodatabox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AeroDataValidatorTest {

    private final AeroDataValidator aeroDataValidator = new AeroDataValidator();

    @Test
    public void shouldReturnFalseWithWrongDateFormat() {
        assertFalse(aeroDataValidator.checkTime("2024/06/01T12:00"));
        assertFalse(aeroDataValidator.checkTime("2024-06-01T99:00"));
    }

    @Test
    public void shouldCatchNullDate() {
        assertFalse(aeroDataValidator.checkTime(null));
    }

    @Test
    public void shouldReturnTrueWithValidDateFormat() {
        assertTrue(aeroDataValidator.checkTime("2024-06-01T10:00"));
        assertTrue(aeroDataValidator.checkTime("2024-06-01T23:59"));
        assertTrue(aeroDataValidator.checkTime("2024-06-02T00:00"));
    }

    @Test
    public void shouldReturnFalseWithTooShortIcaoNumber() {
        assertFalse(aeroDataValidator.checkIcaoFormat("ABC"));
        assertFalse(aeroDataValidator.checkIcaoFormat(""));
        assertFalse(aeroDataValidator.checkIcaoFormat(null));
    }
}