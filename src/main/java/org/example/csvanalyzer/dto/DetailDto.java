package org.example.csvanalyzer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed analysis statistics")
public record DetailDto(
        @Schema(description = "Analysis record ID", example = "1")
        Long id,

        @Schema(description = "Original file name", example = "data.csv")
        String fileName,

        @Schema(description = "File size in bytes", example = "1024000")
        long fileSizeBytes,

        @Schema(description = "Processing time in milliseconds", example = "1500")
        long processingTimeMs,

        @Schema(description = "Minimum value found", example = "10.5")
        double minValue,

        @Schema(description = "Maximum value found", example = "99.9")
        double maxValue,

        @Schema(description = "Arithmetic mean", example = "45.67")
        double mean,

        @Schema(description = "Standard deviation", example = "15.23")
        double stdDev,

        @Schema(description = "Number of valid records processed", example = "1000")
        long recordsCount,

        @Schema(description = "Number of invalid/missing records", example = "5")
        long missingCount,

        @Schema(description = "Number of unique values", example = "950")
        long uniqueCount,

        @Schema(description = "Temporary file path for storage", example = "/app/tmp/tmp_12345.csv")
        String tempFilePath
) {
}