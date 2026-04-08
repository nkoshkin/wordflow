package io.ylab.wordflow.core.validator.impl;

import io.ylab.wordflow.core.validator.IValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileValidator implements IValidator<String> {
    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);

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
