package io.ylab.wordflow.controller;

import io.ylab.wordflow.audit.Auditable;
import io.ylab.wordflow.dto.AnalysisSummaryDto;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.mapper.AnalysisDtoMapper;
import io.ylab.wordflow.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AnalysisDtoMapper analysisDtoMapper;

    @PostMapping("/analyze")
    @Auditable(action = "START_ANALYSIS")
    public ResponseEntity<Map<String, UUID>> startAnalysis(@Valid @RequestBody RequestDto request) {
        UUID id = analysisService.startAnalysis(request);
        return ResponseEntity.accepted().body(Map.of("id", id));
    }

    @GetMapping("/result/{id}")
    @Auditable(action = "GET_RESULT")
    public ResponseEntity<?> getResult(@PathVariable UUID id){
        AnalysisEntity analysis = analysisService.getAnalysis(id);
        if (analysis.getStatus() != AnalysisStatus.COMPLETED) {
            return ResponseEntity.ok(Map.of(
                    "id", analysis.getId().toString(),
                    "status", analysis.getStatus(),
                    "startTime", analysis.getStartTime()
            ));
        }
        return ResponseEntity.ok(analysisDtoMapper.toResponseDto(analysis));
    }

    @GetMapping("/results")
    @Auditable(action = "LIST_RESULTS")
    public ResponseEntity<List<AnalysisSummaryDto>> listResults() {
        List<AnalysisSummaryDto> list = analysisService.getAllAnalyses().stream()
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
