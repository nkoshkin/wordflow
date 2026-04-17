package io.ylab.wordflow.enums;

/**
 * Статус выполнения анализа.
 * Используется для жизненного цикла задачи.
 */
public enum AnalysisStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}
