package io.ylab.wordflow.dto;

import java.util.List;

public record AnalysisResult(
        List<WordCountDto> wordsCount,
        List<ErrorDto> errors,
        Integer processedFiles,
        Long executionTime
) {
}
