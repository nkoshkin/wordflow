package io.ylab.wordflow.dto;

import io.ylab.wordflow.enums.ProcessingMode;

public record RequestDto(
        String directory,
        Integer minLength,
        Integer top,
        String outputFile,
        String stopWordsFile,
        ProcessingMode mode,
        Integer threads
) {
}
