package io.ylab.wordflow.dto;

public record InfoDto(
        String directory,
        Integer minWordLength,
        Integer top
) {
}
