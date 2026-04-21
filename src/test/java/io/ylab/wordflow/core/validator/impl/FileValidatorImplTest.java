package io.ylab.wordflow.core.validator.impl;

import io.ylab.wordflow.core.validator.IValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты для {@link FileValidatorImpl}.
 * Проверяют валидацию существования, типа (обычный файл) и доступности файла.
 */
class FileValidatorImplTest {

    private final IValidator validator = new FileValidatorImpl();

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что для существующего файла метод {@link FileValidatorImpl#validate(String)}
     * не выбрасывает исключений.
     */
    @Test
    void validate_shouldNotThrowWhenFileExists() throws Exception {
        Path file = tempDir.resolve("test.txt");
        file.toFile().createNewFile();

        validator.validate(file.toString());
    }

    /**
     * Проверяет, что для несуществующего файла выбрасывается исключение.
     */
    @Test
    void validate_shouldThrowWhenFileDoesNotExist() {
        assertThatThrownBy(() -> validator.validate("nonexistfile.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File does not exist");
    }

    /**
     * Проверяет, что для пути, указывающего на директорию, выбрасывается исключение.
     */
    @Test
    void validate_shouldThrowWhenPathIsDirectory() {
        assertThatThrownBy(() -> validator.validate(tempDir.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path is not a regular file");
    }
}