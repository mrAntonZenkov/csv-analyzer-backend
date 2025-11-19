package org.example.csvanalyzer.service;

import org.example.csvanalyzer.entity.AnalysisRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface AnalysisService {

    AnalysisRecord analyze(MultipartFile file) throws IOException;

    Page<AnalysisRecord> getHistory(Pageable pageable);

    Optional<AnalysisRecord> findById(Long id);

    void deleteById(Long id) throws IOException;
}