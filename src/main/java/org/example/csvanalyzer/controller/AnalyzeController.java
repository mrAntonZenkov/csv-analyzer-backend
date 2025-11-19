package org.example.csvanalyzer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.csvanalyzer.dto.AnalyzeResponseDto;
import org.example.csvanalyzer.entity.AnalysisRecord;
import org.example.csvanalyzer.exception.FileTypeNotSupportedException;
import org.example.csvanalyzer.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Tag(name = "File Analysis", description = "APIs for uploading and analyzing CSV files")
public class AnalyzeController {

    private final AnalysisService analysisService;

    public AnalyzeController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(path = "/analyze", consumes = "multipart/form-data")
    @Operation(
            summary = "Upload and analyze CSV file",
            description = """
            Upload a CSV file for statistical analysis. The file should contain two columns:
            timestamp and value. Example format:
            
            2025-03-01T10:00:30,105.4
            2025-03-01T10:00:31,95.56
            
            Maximum file size: 50MB
            Supported format: text/csv
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File analyzed successfully",
                    content = @Content(schema = @Schema(implementation = AnalyzeResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file type or empty file"
            ),
            @ApiResponse(
                    responseCode = "413",
                    description = "File size exceeds 50MB limit"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during file processing"
            )
    })
    public ResponseEntity<AnalyzeResponseDto> analyze(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileTypeNotSupportedException("Uploaded file is empty");
        }

        if (!"text/csv".equalsIgnoreCase(file.getContentType())) {
            throw new FileTypeNotSupportedException("Only CSV files are allowed");
        }

        AnalysisRecord saved = analysisService.analyze(file);

        return ResponseEntity.ok(new AnalyzeResponseDto(saved.getId()));
    }
}