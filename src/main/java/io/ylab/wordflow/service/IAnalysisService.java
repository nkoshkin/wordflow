package io.ylab.wordflow.service;

import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;

import java.util.List;
import java.util.UUID;

public interface IAnalysisService {

    UUID startAnalysis(RequestDto request);
    AnalysisEntity getAnalysis(UUID id);
    List<AnalysisEntity> getAllAnalysis();
    void saveAnalysisResult(UUID analysisId, AnalysisResult result);
}
