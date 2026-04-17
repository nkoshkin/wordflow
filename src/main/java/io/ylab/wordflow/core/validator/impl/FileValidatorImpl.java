package io.ylab.wordflow.core.validator.impl;

import io.ylab.wordflow.core.validator.IValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Валидатор пути к файлу.
 * Проверяет, что файл существует, является обычным файлом и доступен для чтения.
 */
@Component
public class FileValidatorImpl implements IValidator<String> {
    private static final Logger logger = LoggerFactory.getLogger(FileValidatorImpl.class);

    /**
     * Проверяет корректность пути к файлу.
     *
     * <p>Критерии валидности:
     * <ul>
     *   <li>путь не должен быть {@code null} или пустым</li>
     *   <li>файл должен существовать в файловой системе</li>
     *   <li>путь должен указывать на обычный файл (не директорию)</li>
     *   <li>файл должен быть доступен для чтения</li>
     * </ul>
     * </p>
     *
     * @param value путь к файлу
     * @throws IllegalArgumentException если файл не существует, не является обычным файлом
     *                                  или недоступен для чтения
     */
    @Override
    public void validate(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("File path is null or empty");
        Path path = Paths.get(value);
        if (!Files.exists(path)) throw new IllegalArgumentException("File does not exist: " + value);
        if (!Files.isRegularFile(path)) throw new IllegalArgumentException("Path is not a regular file: " + value);
        if (!Files.isReadable(path)) throw new IllegalArgumentException("File is not readable: " + value);
        logger.debug("File is valid: {}", value);
    }
}
