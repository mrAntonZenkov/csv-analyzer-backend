package org.example.csvanalyzer.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String error,
        int status,
        Instant timestamp
) {
}