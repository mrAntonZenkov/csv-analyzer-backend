package org.example.csvanalyzer.mapper;

import org.example.csvanalyzer.dto.DetailDto;
import org.example.csvanalyzer.dto.HistoryDto;
import org.example.csvanalyzer.entity.AnalysisRecord;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnalysisRecordMapper {

    HistoryDto toHistoryDto(AnalysisRecord record);

    DetailDto toDetailDto(AnalysisRecord record);
}