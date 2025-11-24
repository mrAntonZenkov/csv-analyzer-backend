package org.example.csvanalyzer.repository;

import org.example.csvanalyzer.entity.AnalysisRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord, Long> {
    List<AnalysisRecord> findAllByOrderByIdAsc(Pageable pageable);
}