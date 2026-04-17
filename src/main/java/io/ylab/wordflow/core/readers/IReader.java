package io.ylab.wordflow.core.readers;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Интерфейс для чтения слов из ресурса (файла).
 * Предоставляет метод {@link #readWords(String)} для извлечения всех слов
 * из указанного файла с приведением к нижнему регистру.
 *
 * <p>Содержит константу {@link #WORD_PATTERN} – регулярное выражение,
 * определяющее символы, из которых могут состоять слова.</p>
 */
public interface IReader {

    /**
     * Регулярное выражение для поиска слов.
     * Поддерживает буквы английского и русского алфавитов.
     */
    Pattern WORD_PATTERN = Pattern.compile("[a-zA-Zа-яА-Я]+");

    /**
     * Извлекает все слова из файла, приводит их к нижнему регистру и возвращает списком.
     *
     * <p>Слова выделяются с помощью {@link #WORD_PATTERN}.</p>
     *
     * @param resource путь к файлу (не может быть {@code null})
     * @return список слов в порядке их появления в тексте
     * @throws RuntimeException если файл не удаётся прочитать (например, отсутствует, нет прав)
     */
    List<String> readWords(String resource);
}
