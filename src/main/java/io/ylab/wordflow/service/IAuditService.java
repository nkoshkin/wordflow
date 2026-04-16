package io.ylab.wordflow.service;

import java.util.UUID;

public interface IAuditService {
    void logAnalysis(String action, String params, UUID analysisId);
}
