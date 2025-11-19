package org.example.csvanalyzer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Analysis history item")
public record HistoryDto(
        @Schema(description = "Analysis record ID", example = "1")
        Long id,

        @Schema(description = "Original file name", example = "data.csv")
        String fileName,

        @Schema(description = "File size in bytes", example = "1024000")
        long fileSizeBytes,

        @Schema(description = "Processing time in milliseconds", example = "1500")
        long processingTimeMs,

        @Schema(description = "Arithmetic mean", example = "45.67")
        double mean,

        @Schema(description = "Standard deviation", example = "15.23")
        double stdDev
) {
}