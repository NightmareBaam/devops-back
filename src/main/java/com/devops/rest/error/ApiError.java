package com.devops.rest.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        ApiErrorCode code,
        String message,
        String path,
        String correlationId,
        List<String> details
) {

    public ApiError {
        timestamp = timestamp == null ? Instant.now() : timestamp;
        details = details == null ? List.of() : List.copyOf(details);
    }
}
