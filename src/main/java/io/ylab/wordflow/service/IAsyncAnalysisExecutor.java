package io.ylab.wordflow.service;

import io.ylab.wordflow.dto.RequestDto;

import java.util.UUID;

public interface IAsyncAnalysisExecutor {
    void execute(UUID analysisId, RequestDto request);
}
