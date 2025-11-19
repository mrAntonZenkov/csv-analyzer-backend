package org.example.csvanalyzer.repository;

import org.example.csvanalyzer.entity.AnalysisRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord, Long> {
}