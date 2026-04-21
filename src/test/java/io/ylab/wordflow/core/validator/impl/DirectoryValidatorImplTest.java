package io.ylab.wordflow.core.validator.impl;

import io.ylab.wordflow.core.validator.IValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты для {@link DirectoryValidatorImpl}.
 * Проверяют валидацию существования, типа и доступности директории.
 */
class DirectoryValidatorImplTest {

    private final IValidator validator = new DirectoryValidatorImpl();

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что для существующей директории метод {@link DirectoryValidatorImpl#validate(String)}
     * не выбрасывает исключений.
     */
    @Test
    void validate_shouldNotThrowWhenDirectoryExists() {
        validator.validate(tempDir.toString());
    }

    /**
     * Проверяет, что для несуществующей директории выбрасывается исключение с сообщением
     * "Directory does not exist".
     */
    @Test
    void validate_shouldThrowWhenDirectoryDoesNotExist() {
        assertThatThrownBy(() -> validator.validate("/nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Directory does not exist");
    }

    /**
     * Проверяет, что для пути, указывающего на файл (а не директорию), выбрасывается исключение.
     */
    @Test
    void validate_shouldThrowWhenPathIsNotDirectory() throws Exception {
        Path file = tempDir.resolve("file.txt");
        file.toFile().createNewFile();

        assertThatThrownBy(() -> validator.validate(file.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path is not directory");
    }
}