package io.ylab.wordflow.dto;

import io.ylab.wordflow.enums.ProcessingMode;

/**
 * DTO для передачи параметров анализа текстов.
 *
 * @param directory         путь к директории с текстовыми файлами
 * @param minLength         минимальная длина слова
 * @param top               количество топ слов
 * @param stopWordsFile     путь к файлу со стоп-словами
 * @param outputFile        путь для сохранения результата в файл
 * @param mode              режим обработки (SINGLE или MULTI)
 * @param threads           количество потоков для многопоточного режима
 */
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
