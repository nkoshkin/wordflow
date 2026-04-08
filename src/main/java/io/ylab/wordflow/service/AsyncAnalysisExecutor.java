package io.ylab.wordflow.service;

import io.ylab.wordflow.core.analysis.FileAnalysisService;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.entity.ErrorEntity;
import io.ylab.wordflow.entity.WordCountEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAnalysisExecutor {

    private final AnalysisRepository analysisRepository;
    private final FileAnalysisService fileAnalysisService;

    @Async
    @Transactional
    public void execute(UUID analysisId, RequestDto request) {
        AnalysisEntity analysis = analysisRepository.findById(analysisId).orElseThrow();
        analysis.setStatus(AnalysisStatus.RUNNING);
        analysisRepository.save(analysis);
        long startTime = System.currentTimeMillis();

        AnalysisResult result = fileAnalysisService.performAnalysis(request);
        if (!result.wordsCount().isEmpty()) {
            analysis.setWords(result.wordsCount().stream()
                    .map(wc -> WordCountEntity.builder()
                            .analysis(analysis)
                            .word(wc.word())
                            .count(wc.count())
                            .build())
                    .collect(Collectors.toList()));
        }
        if (!result.errors().isEmpty()) {
            analysis.setErrors(result.errors().stream()
                    .map(e -> ErrorEntity.builder()
                            .analysis(analysis)
                            .file(e.file())
                            .message(e.message())
                            .build())
                    .collect(Collectors.toList()));
        }
        AnalysisStatus status = getStatusFromresult(result);
        analysis.setStatus(status);
        analysis.setEndTime(LocalDateTime.now());
        analysis.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        analysisRepository.save(analysis);
        log.info("Analysis {} finished with status {} in {} ms",
                analysis.getId(), status, analysis.getExecutionTimeMs());
    }

    private AnalysisStatus getStatusFromresult(AnalysisResult result) {
        boolean hasDirectoryError = result.errors().stream()
                .anyMatch(e -> e.file().equals(result.errors().getFirst().file()) &&
                        e.message().contains("Directory does not exist"));

        return (hasDirectoryError) ? AnalysisStatus.FAILED : AnalysisStatus.COMPLETED;
    }
}
