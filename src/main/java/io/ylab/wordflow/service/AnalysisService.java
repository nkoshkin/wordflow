package io.ylab.wordflow.service;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AsyncAnalysisExecutor asyncExecutor;
    private final AuditService auditService;

    @Transactional
    public UUID startAnalysis(RequestDto request) {
        AnalysisEntity entity = AnalysisEntity.builder()
                .directory(request.directory())
                .minLength(request.minLength())
                .top(request.top())
                .mode(request.mode().name().toLowerCase())
                .threads(request.threads())
                .stopWordsFile(request.stopWordsFile())
                .outputFile(request.outputFile())
                .status(AnalysisStatus.PENDING)
                .startTime(LocalDateTime.now())
                .build();

        entity = analysisRepository.save(entity);
        UUID id = entity.getId();

        asyncExecutor.execute(id, request);

        log.info("Analysis started with id: {}", id);
        return id;
    }

    public AnalysisEntity getAnalysis(UUID id) {
        return analysisRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found"));
    }

    public List<AnalysisEntity> getAllAnalyses() {
        return analysisRepository.findAllByOrderByStartTimeDesc();
    }
}