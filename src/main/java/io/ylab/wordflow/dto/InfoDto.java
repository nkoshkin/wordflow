package io.ylab.wordflow.dto;

public record InfoDto(
        String directory,
        Integer minLength,
        Integer top,
        String mode,
        Integer threads,
        Integer processedFiles,
        long executionTimeMs
) {
}
