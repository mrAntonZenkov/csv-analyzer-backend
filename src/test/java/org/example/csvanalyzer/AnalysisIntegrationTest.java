package org.example.csvanalyzer;

import org.example.csvanalyzer.entity.AnalysisRecord;
import org.example.csvanalyzer.repository.AnalysisRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AnalysisIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalysisRecordRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }


    @Test
    void testAnalyzeValidCsv() throws Exception {
        String csvContent = """
                timestamp,value
                2025-03-01T10:00:30,100
                2025-03-01T10:00:31,200
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "sample.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/analyze").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        AnalysisRecord record = repository.findAll().get(0);
        assertThat(record.getRecordsCount()).isEqualTo(2);
        assertThat(record.getMinValue()).isEqualTo(100);
        assertThat(record.getMaxValue()).isEqualTo(200);
        assertThat(record.getMean()).isEqualTo(150.0);
        assertThat(record.getUniqueCount()).isEqualTo(2);
        assertThat(record.getMissingCount()).isEqualTo(0);
        assertThat(record.getProcessingTimeMs()).isPositive();
    }

    @Test
    void testAnalyzeEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv", new byte[0]
        );

        mockMvc.perform(multipart("/analyze").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAnalyzeInvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "sample.txt", "text/plain", "data".getBytes()
        );

        mockMvc.perform(multipart("/analyze").file(file))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testHistoryPaginationAndSorting() throws Exception {
        for (long i = 1; i <= 12; i++) {
            AnalysisRecord record = new AnalysisRecord();
            record.setFileName("file" + i + ".csv");
            record.setFileSizeBytes(100L + i);
            record.setProcessingTimeMs(50L + i);
            record.setMean(i * 10.0);
            record.setStdDev(i * 0.5);
            record.setRecordsCount(i);
            record.setMissingCount(0L);
            record.setUniqueCount(i);
            repository.save(record);
        }

        mockMvc.perform(get("/history?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        // GET second page (5 items)
        mockMvc.perform(get("/history?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }


    @Test
    void testGetDetailExistingRecord() throws Exception {
        AnalysisRecord record = new AnalysisRecord();
        record.setFileName("file.csv");
        record.setFileSizeBytes(123L);
        record.setRecordsCount(2L);
        repository.save(record);

        mockMvc.perform(get("/history/" + record.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(record.getId()));
    }

    @Test
    void testGetDetailNonExistingRecord() throws Exception {
        mockMvc.perform(get("/history/9999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testDeleteExistingRecord() throws Exception {
        AnalysisRecord record = new AnalysisRecord();
        record.setFileName("file.csv");
        repository.save(record);

        mockMvc.perform(delete("/history/" + record.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.existsById(record.getId())).isFalse();
    }

    @Test
    void testDeleteNonExistingRecord() throws Exception {
        mockMvc.perform(delete("/history/9999"))
                .andExpect(status().isNotFound());
    }
}
