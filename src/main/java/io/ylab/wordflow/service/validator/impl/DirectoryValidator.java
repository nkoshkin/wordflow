package io.ylab.wordflow.service.validator.impl;

import io.ylab.wordflow.service.validator.IValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DirectoryValidator implements IValidator<String> {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryValidator.class);

    @Override
    public void validate(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Directory is null or empty");
        Path path = Paths.get(value);
        if (!Files.exists(path)) throw new IllegalArgumentException("Directory does not exist: " + value);
        if (!Files.isDirectory(path)) throw new IllegalArgumentException("Path is not directory: " + value);
        if (!Files.isReadable(path)) throw new IllegalArgumentException("Directory is not readable: " + value);
        logger.debug("Directory is valid: {}", value);
    }
}
