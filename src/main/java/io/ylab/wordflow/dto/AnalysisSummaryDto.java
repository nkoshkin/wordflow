package io.ylab.wordflow.dto;

import io.ylab.wordflow.enums.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * DTO для представления общей информации об анализе.
 *
 * @param id идентификатор анализа
 * @param status статус выполнения анализа (PENDING, RUNNING, COMPLETED, FAILED)
 * @param startTime время начала анализа
 * @param directory путь к директории для анализа
 */
public record AnalysisSummaryDto(
        UUID id,
        AnalysisStatus status,
        LocalDateTime startTime,
        String directory
) {
}
