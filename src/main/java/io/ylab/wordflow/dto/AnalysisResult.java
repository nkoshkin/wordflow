package io.ylab.wordflow.dto;

import java.util.List;

/**
 * DTO, Результат выполнения анализа.
 *
 * @param wordsCount список топ слов с частотами
 * @param errors список ошибок, возникших при обработке файлов
 * @param processedFiles количество обработанных файлов
 * @param executionTime время выполнения анализа в миллисекундах
 */
public record AnalysisResult(
        List<WordCountDto> wordsCount,
        List<ErrorDto> errors,
        Integer processedFiles,
        Long executionTime
) {
}
