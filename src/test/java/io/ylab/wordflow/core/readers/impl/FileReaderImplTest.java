package io.ylab.wordflow.core.readers.impl;

import io.ylab.wordflow.core.validator.impl.FileValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты для {@link FileReaderImpl}.
 * Проверяют чтение слов из файла, поддержку кириллицы и обработку ошибок.
 */
@ExtendWith(MockitoExtension.class)
class FileReaderImplTest {

    @Mock
    private FileValidatorImpl fileValidator;

    @InjectMocks
    private FileReaderImpl reader;

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что метод {@link FileReaderImpl#readWords(String)} корректно извлекает
     * английские и русские слова, приводит их к нижнему регистру.
     */
    @Test
    void readWords_shouldExtractWordsAndConvertToLowerCase() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello world! Привет мир. Java. 123");

        List<String> words = reader.readWords(file.toString());

        assertThat(words).containsExactly("hello", "world", "привет", "мир", "java");
    }

    /**
     * Проверяет, что для пустого файла возвращается пустой список.
     */
    @Test
    void readWords_whenEmptyFile_shouldReturnEmptyList() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        List<String> words = reader.readWords(file.toString());

        assertThat(words).isEmpty();
    }

    /**
     * Проверяет, что для несуществующего файла выбрасывается исключение.
     */
    @Test
    void readWords_whenFileNotExists_shouldThrowRuntimeException() {
        assertThatThrownBy(() -> reader.readWords("/nonexistfile.txt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed read file");
    }
}