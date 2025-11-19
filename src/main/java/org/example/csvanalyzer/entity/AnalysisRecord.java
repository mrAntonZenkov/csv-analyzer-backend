package org.example.csvanalyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "analysis_record")
@Getter
@Setter
public class AnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSizeBytes;

    @Column(name = "processing_time")
    private Long processingTimeMs;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column
    private Double mean;

    @Column(name = "std_dev")
    private Double stdDev;

    @Column(name = "records_count")
    private Long recordsCount;

    @Column(name = "missing_count")
    private Long missingCount;

    @Column(name = "unique_count")
    private Long uniqueCount;

    @Column(name = "temp_file_path")
    private String tempFilePath;
}