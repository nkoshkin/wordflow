package io.ylab.wordflow.dto;

import java.util.List;

/**
 * DTO для ответа анализа.
 * Содержит информацию о запросе и выполнении, список топ-слов и ошибки.
 *
 * @param infoDto метаинформация об анализе (директория, параметры, статистика)
 * @param words список топ-слов с частотами
 * @param errors список ошибок, возникших при выполнении анализа
 */
public record ResponseDto(
        InfoDto infoDto,
        List<WordCountDto> words,
        List<ErrorDto> errors
) {
}
