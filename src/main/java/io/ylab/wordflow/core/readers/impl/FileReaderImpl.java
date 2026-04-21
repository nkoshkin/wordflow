package io.ylab.wordflow.core.readers.impl;

import io.ylab.wordflow.core.readers.IReader;
import io.ylab.wordflow.core.validator.impl.FileValidatorImpl;
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

/**
 * Реализация {@link IReader} для чтения слов из файловой системы.
 * Перед чтением выполняет валидацию файла через {@link FileValidatorImpl}.
 *
 * <p>Использует {@link Files#readString(Path)} для загрузки содержимого
 * и регулярное выражение {@link IReader#WORD_PATTERN} для выделения слов.</p>
 *
 * @see IReader
 * @see FileValidatorImpl
 */
@Component
public class FileReaderImpl implements IReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderImpl.class);

    @Autowired
    FileValidatorImpl fileValidatorImpl;

    /**
     * {@inheritDoc}
     * Извлекает слова из указанного файла.
     *
     * <p>Перед чтением вызывает {@link FileValidatorImpl#validate(String)}.
     * Если файл валиден, содержимое загружается, разбивается на слова по шаблону
     * {@link IReader#WORD_PATTERN}, все слова приводятся к нижнему регистру.
     * В случае пустого файла возвращается пустой список.</p>
     *
     * @param resource путь к файлу (не может быть {@code null})
     * @return список слов в порядке появления
     * @throws IllegalArgumentException если файл не прошёл валидацию (отсутствует, не читается и т.д.)
     * @throws RuntimeException         если возникла ошибка ввода-вывода при чтении файла
     */
    @Override
    public List<String> readWords(String resource) {

        fileValidatorImpl.validate(resource);

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
