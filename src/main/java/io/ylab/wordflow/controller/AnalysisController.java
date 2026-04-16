package io.ylab.wordflow.controller;

import io.ylab.wordflow.audit.Auditable;
import io.ylab.wordflow.dto.AnalysisSummaryDto;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.mapper.AnalysisDtoMapper;
import io.ylab.wordflow.service.IAnalysisService;
import io.ylab.wordflow.service.IAsyncAnalysisExecutor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final IAnalysisService analysisService;
    private final IAsyncAnalysisExecutor asyncAnalysisExecutor;
    private final AnalysisDtoMapper analysisDtoMapper;

    @PostMapping("/analyze")
    @Auditable(action = "START_ANALYSIS")
    public ResponseEntity<Map<String, UUID>> startAnalysis(@Valid @RequestBody RequestDto request) {
        UUID id = analysisService.startAnalysis(request);
        asyncAnalysisExecutor.execute(id, request);
        return ResponseEntity.accepted().body(Map.of("id", id));
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<?> getResult(@PathVariable UUID id) {
        try {
            AnalysisEntity entity = analysisService.getAnalysis(id);
            if (entity.getStatus() != AnalysisStatus.COMPLETED) {
                return ResponseEntity.ok(Map.of(
                        "id", entity.getId().toString(),
                        "status", entity.getStatus(),
                        "startTime", entity.getStartTime()
                ));
            }
            return ResponseEntity.ok(analysisDtoMapper.toResponseDto(entity));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason() != null ? e.getReason() : "Unknown error"));
        }
    }

    @GetMapping("/results")
    @Auditable(action = "LIST_RESULTS")
    public ResponseEntity<List<AnalysisSummaryDto>> listResults() {
        List<AnalysisSummaryDto> list = analysisService.getAllAnalysis().stream()
                .map(a -> new AnalysisSummaryDto(
                        a.getId(),
                        a.getStatus(),
                        a.getStartTime(),
                        a.getDirectory()
                ))
                .toList();
        return ResponseEntity.ok(list);
    }
}
