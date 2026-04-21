package io.ylab.wordflow.core.processor.impl;

import io.ylab.wordflow.core.readers.IReader;
import io.ylab.wordflow.dto.WordCountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Тесты для {@link FileProcessorImpl}.
 * Проверяют последовательную и параллельную обработку файлов, фильтрацию слов.
 */
@ExtendWith(MockitoExtension.class)
class FileProcessorImplTest {

    @Mock
    private IReader wordReader;

    @InjectMocks
    private FileProcessorImpl fileProcessor;

    /**
     * Проверяет, что при последовательной обработке (threads = 1)
     * все файлы обрабатываются, а результаты корректно суммируются.
     */
    @Test
    void processFiles_sequential_shouldProcessAllFiles() {
        Path file1 = Path.of("file1.txt");
        Path file2 = Path.of("file2.txt");
        when(wordReader.readWords(file1.toString())).thenReturn(List.of("word1", "word1", "word2"));
        when(wordReader.readWords(file2.toString())).thenReturn(List.of("word1", "word2", "word2"));

        List<WordCountDto> result = fileProcessor.processFiles(
                List.of(file1, file2), 3, Set.of(), List.of(), 1, 5
        );

        assertThat(result).containsExactlyInAnyOrder(
                new WordCountDto("word1", 3), new WordCountDto("word2", 3)
        );
    }

    /**
     * Проверяет, что при параллельной обработке (threads > 1)
     * результаты также корректно объединяются.
     */
    @Test
    void processFiles_parallel_shouldMergeResults() {
        Path file1 = Path.of("file1.txt");
        Path file2 = Path.of("file2.txt");
        when(wordReader.readWords(file1.toString())).thenReturn(List.of("word1", "word1"));
        when(wordReader.readWords(file2.toString())).thenReturn(List.of("word1", "word2"));

        List<WordCountDto> result = fileProcessor.processFiles(
                List.of(file1, file2), 3, Set.of(), List.of(), 2, 5
        );

        assertThat(result).containsExactlyInAnyOrder(
                new WordCountDto("word1", 3), new WordCountDto("word2", 1)
        );
    }

    /**
     * Проверяет фильтрацию слов по минимальной длине и стоп-словам.
     */
    @Test
    void processFiles_shouldFilterByLengthAndStopWords() {
        Path file = Path.of("file.txt");
        when(wordReader.readWords(file.toString())).thenReturn(List.of("a", "ab", "abc", "abcd", "the", "and"));
        Set<String> stopWords = Set.of("the", "and");

        List<WordCountDto> result = fileProcessor.processFiles(
                List.of(file), 3, stopWords, List.of(), 1, 10
        );

        assertThat(result).containsExactlyInAnyOrder(
                new WordCountDto("abc", 1), new WordCountDto("abcd", 1)
        );
    }
}