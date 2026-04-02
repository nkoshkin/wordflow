package io.ylab.wordflow.dto;

public record RequestDto(
        String directory,
        Integer minWordLength,
        Integer top,
        String outputFile,
        String stopWordsFile
) {
}
