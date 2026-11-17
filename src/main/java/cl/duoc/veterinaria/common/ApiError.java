package cl.duoc.veterinaria.common;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiError(int status, String error, String message, String timestamp) {

    public static ApiError de(HttpStatus status, String message) {
        return new ApiError(status.value(), status.getReasonPhrase(), message, Instant.now().toString());
    }
}