package io.ylab.wordflow.dto;

/**
 * DTO для описания ошибки.
 *
 * @param file имя файла, в котором произошла ошибка
 * @param message текст ошибки
 */
public record ErrorDto(
        String file,
        String message
) {
}
