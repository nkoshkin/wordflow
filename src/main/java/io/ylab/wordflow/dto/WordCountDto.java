package io.ylab.wordflow.dto;

/**
 * DTO, представляющий слово и его частоту.
 *
 * @param word слово
 * @param count количество
 */
public record WordCountDto(
        String word,
        Integer count
) {
}
