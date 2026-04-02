package io.ylab.wordflow.service.readers.impl;

import io.ylab.wordflow.service.readers.Ireader;
import io.ylab.wordflow.service.validator.impl.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

@Component
public class FileReaderImpl implements Ireader {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderImpl.class);

    @Autowired
    FileValidator fileValidator;

    @Override
    public List<String> readWords(String resource) {

        fileValidator.validate(resource);

        try{
            Path path = Paths.get(resource);
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) return Collections.emptyList();
            List<String> words = new ArrayList<>();
            Matcher matcher = WORD_PATTERN.matcher(content);
            while(matcher.find()){
                words.add(matcher.group().toLowerCase());
            }
            logger.info("Read {} words from file {}", words.size(), resource);
            return words;

        } catch (IOException e) {
            logger.error("Failed read file: {}", resource);
            throw new RuntimeException("Failed read file: " + resource, e);
        }
    }
}
