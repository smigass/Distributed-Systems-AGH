package com.agh.student.smigas.radar.exceptions;

public class ApiException extends RuntimeException{
    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return switch (status) {
            case 401, 500 -> 502;
            default -> status;
        };
    }

    public String getMessage() {
        return switch (status) {
            case 401 -> "Authentication error";
            case 404 -> "Resource not found";
            case 500 -> "Internal server error";
            case 502 -> "Error fetching data from external API";
            default -> super.getMessage();
        };
    }
}
