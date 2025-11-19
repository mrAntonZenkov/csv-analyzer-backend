package org.example.csvanalyzer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.csvanalyzer.dto.DetailDto;
import org.example.csvanalyzer.dto.HistoryDto;
import org.example.csvanalyzer.entity.AnalysisRecord;
import org.example.csvanalyzer.exception.RecordNotFoundException;
import org.example.csvanalyzer.mapper.AnalysisRecordMapper;
import org.example.csvanalyzer.service.AnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/history")
@Tag(name = "Analysis History", description = "APIs for retrieving and managing analysis history")
public class HistoryController {

    private final AnalysisService analysisService;
    private final AnalysisRecordMapper mapper;

    public HistoryController(AnalysisService analysisService, AnalysisRecordMapper mapper) {
        this.analysisService = analysisService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
            summary = "Get analysis history",
            description = "Retrieve paginated history of last 10 analyses (5 records per page)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "History retrieved successfully"
            )
    })
    public ResponseEntity<List<HistoryDto>> getHistory(
            @Parameter(description = "Page number (0-based), default: 0", example = "0")
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<AnalysisRecord> pageResult = analysisService.getHistory(pageable);

        List<HistoryDto> list = pageResult.stream()
                .map(mapper::toHistoryDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get analysis details",
            description = "Retrieve detailed statistics for a specific analysis record"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DetailDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Analysis record not found"
            )
    })
    public ResponseEntity<DetailDto> getDetail(
            @Parameter(description = "Analysis record ID", required = true, example = "1")
            @PathVariable Long id) {
        AnalysisRecord record = analysisService.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        return ResponseEntity.ok(mapper.toDetailDto(record));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete analysis record",
            description = "Delete a specific analysis record from history"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Record deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Analysis record not found"
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Analysis record ID", required = true, example = "1")
            @PathVariable Long id) throws IOException {
        analysisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}