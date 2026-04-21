package io.ylab.wordflow.dto;

/**
 * Информация об анализе.
 * Включает параметры запроса и статистику выполнения.
 *
 * @param directory путь к директории
 * @param minLength минимальная длина слова
 * @param top количество топ-слов
 * @param mode режим обработки
 * @param threads количество потоков
 * @param processedFiles количество обработанных файлов
 * @param executionTimeMs время выполнения анализа в миллисекундах
 */
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
