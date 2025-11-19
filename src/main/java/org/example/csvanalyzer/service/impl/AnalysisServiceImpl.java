package org.example.csvanalyzer.service.impl;

import org.example.csvanalyzer.config.StorageProperties;
import org.example.csvanalyzer.entity.AnalysisRecord;
import org.example.csvanalyzer.exception.FileProcessingException;
import org.example.csvanalyzer.exception.RecordNotFoundException;
import org.example.csvanalyzer.repository.AnalysisRecordRepository;
import org.example.csvanalyzer.service.AnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisRecordRepository repository;
    private final Path tempDir;

    public AnalysisServiceImpl(AnalysisRecordRepository repository,
                               StorageProperties storageProperties) throws IOException {
        this.repository = repository;
        this.tempDir = Paths.get(storageProperties.getTmpDir());
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
    }

    @Override
    public AnalysisRecord analyze(MultipartFile file) {
        String tempFileName = "tmp_" + UUID.randomUUID() + ".csv";
        Path tempFilePath = tempDir.resolve(tempFileName);

        try {
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to save temp file");
        }

        long[] recordsCount = {0};
        long[] missingCount = {0};
        double[] minValue = {Double.MAX_VALUE};
        double[] maxValue = {Double.MIN_VALUE};
        double[] mean = {0.0};
        double[] m2 = {0.0};
        HashSet<Double> uniqueValues = new HashSet<>();

        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = Files.newBufferedReader(tempFilePath)) {
            reader.lines()
                    .skip(1)
                    .map(line -> line.split(","))
                    .forEach(parts -> {
                        if (parts.length != 2) {
                            missingCount[0]++;
                            return;
                        }

                        double value;
                        try {
                            value = Double.parseDouble(parts[1].trim());
                        } catch (NumberFormatException ex) {
                            missingCount[0]++;
                            return;
                        }

                        recordsCount[0]++;
                        minValue[0] = Math.min(minValue[0], value);
                        maxValue[0] = Math.max(maxValue[0], value);
                        uniqueValues.add(value);

                        // Welford алгоритм
                        double delta = value - mean[0];
                        mean[0] += delta / recordsCount[0];
                        m2[0] += delta * (value - mean[0]);
                    });
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read CSV file");
        }

        long endTime = System.currentTimeMillis();
        long processingTimeMs = endTime - startTime;

        double stdDev = recordsCount[0] > 1 ? Math.sqrt(m2[0] / (recordsCount[0] - 1)) : 0.0;

        AnalysisRecord record = new AnalysisRecord();
        record.setFileName(file.getOriginalFilename());
        record.setFileSizeBytes(file.getSize());
        record.setProcessingTimeMs(processingTimeMs);
        record.setMinValue(recordsCount[0] > 0 ? minValue[0] : 0.0);
        record.setMaxValue(recordsCount[0] > 0 ? maxValue[0] : 0.0);
        record.setMean(recordsCount[0] > 0 ? mean[0] : 0.0);
        record.setStdDev(recordsCount[0] > 0 ? stdDev : 0.0);
        record.setRecordsCount(recordsCount[0]);
        record.setMissingCount(missingCount[0]);
        record.setUniqueCount((long) uniqueValues.size());
        record.setTempFilePath(tempFilePath.toString());

        AnalysisRecord saved = repository.save(record);

        maintainLast10();

        return saved;
    }

    @Override
    public Page<AnalysisRecord> getHistory(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<AnalysisRecord> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));

        repository.deleteById(id);
    }

    private void maintainLast10() {
        long count = repository.count();
        if (count <= 10){
            return;
        }

        List<AnalysisRecord> toDelete = repository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(AnalysisRecord::getId))
                .limit(count - 10)
                .toList();

        for (AnalysisRecord r : toDelete) {
            repository.delete(r);
        }
    }
}