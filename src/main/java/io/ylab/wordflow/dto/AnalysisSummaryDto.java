package io.ylab.wordflow.dto;

import io.ylab.wordflow.enums.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnalysisSummaryDto(
        UUID id,
        AnalysisStatus status,
        LocalDateTime startTime,
        String directory
) {
}
