package io.ylab.wordflow.core.processor;

import io.ylab.wordflow.core.readers.IReader;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.WordCountDto;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Интерфейс для многопоточной/однопоточной обработки списка файлов.
 * Предоставляет метод {@link #processFiles(List, Integer, Set, List, Integer, Integer)},
 * который выполняет чтение слов из файлов, фильтрацию по длине и стоп-словам,
 * подсчёт частоты и возвращает топ-N слов.
 *
 * <p>Реализации должны обеспечивать потокобезопасность при параллельной обработке.</p>
 */
public interface IFileProcessor {

    /**
     * Обрабатывает список файлов и возвращает топ слов по частоте.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Для каждого файла извлекаются слова через {@link IReader}.</li>
     *   <li>Слова фильтруются по минимальной длине и исключаются стоп-слова.</li>
     *   <li>Подсчитывается частота каждого слова.</li>
     *   <li>Результаты всех файлов объединяются (для параллельного режима используется {@link ConcurrentHashMap}).</li>
     *   <li>Слова сортируются по убыванию частоты и ограничиваются top-N.</li>
     * </ol>
     * </p>
     *
     * @param files список путей к файлам (не может быть {@code null})
     * @param minWordLength минимальная длина слова (включительно)
     * @param stopWords множество стоп-слов (может быть {@code null} или пустым)
     * @param errors список для накопления ошибок (будет изменён)
     * @param threads количество потоков: 1 – последовательная обработка, >1 – параллельная
     * @param top количество топ слов для возврата
     * @return список {@link WordCountDto}, отсортированный по убыванию частоты (не более top)
     */
    List<WordCountDto> processFiles(List<Path> files,
                                    Integer minWordLength,
                                    Set<String> stopWords,
                                    List<ErrorDto> errors,
                                    Integer threads,
                                    Integer top);
}
