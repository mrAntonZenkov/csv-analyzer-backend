package org.example.csvanalyzer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after successful file analysis")
public record AnalyzeResponseDto(
        @Schema(description = "Unique identifier of the analysis record", example = "1")
        Long id) {
}
