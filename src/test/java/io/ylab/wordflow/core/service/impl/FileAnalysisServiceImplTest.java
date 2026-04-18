package io.ylab.wordflow.core.service.impl;

import io.ylab.wordflow.core.processor.IFileProcessor;
import io.ylab.wordflow.core.readers.IReader;
import io.ylab.wordflow.core.validator.impl.DirectoryValidatorImpl;
import io.ylab.wordflow.core.validator.impl.FileValidatorImpl;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.WordCountDto;
import io.ylab.wordflow.enums.ProcessingMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link FileAnalysisServiceImpl}.
 * Проверяют бизнес-логику анализа текстов: валидацию директории, сбор файлов,
 * фильтрацию стоп-слов, обработку через {@link IFileProcessor} и формирование результата.
 */
@ExtendWith(MockitoExtension.class)
class FileAnalysisServiceImplTest {

    @Mock
    private IFileProcessor fileProcessor;

    @Mock
    private IReader ireader;

    @Mock
    private DirectoryValidatorImpl directoryValidatorImpl;

    @Mock
    private FileValidatorImpl fileValidatorImpl;

    @InjectMocks
    private FileAnalysisServiceImpl service;

    /**
     * Проверяет, что при неверной директории метод {@link FileAnalysisServiceImpl#performAnalysis(RequestDto)}
     * возвращает {@link AnalysisResult} с ошибкой, а {@link IFileProcessor} не вызывается.
     */
    @Test
    void performAnalysis_whenDirectoryInvalid_shouldReturnError() {
        RequestDto request = new RequestDto("/invalid", 3, 10, null, null, ProcessingMode.MULTI, 2);
        doThrow(new IllegalArgumentException("Directory does not exist"))
                .when(directoryValidatorImpl).validate("/invalid");

        AnalysisResult result = service.performAnalysis(request);

        assertThat(result.wordsCount()).isEmpty();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().getFirst().message()).contains("Directory does not exist");
        assertThat(result.processedFiles()).isZero();
        assertThat(result.executionTime()).isGreaterThanOrEqualTo(0);
        verify(fileProcessor, never()).processFiles(any(), anyInt(), any(), any(), anyInt(), anyInt());
    }

    /**
     * Проверяет, что при наличии валидных файлов метод вызывает {@link IFileProcessor#processFiles(List, Integer, Set, List, Integer, Integer)} (...)}
     * и возвращает результат с правильными данными.
     * Используется временная директория с реальными .txt файлами.
     */
    @Test
    void performAnalysis_whenValidFiles_shouldProcessAndReturnResult(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve("test1.txt");
        Path file2 = tempDir.resolve("test2.txt");
        Files.writeString(file1, "java java spring");
        Files.writeString(file2, "java spring spring");
        String dirPath = tempDir.toString();

        RequestDto request = new RequestDto(dirPath, 3, 10, null, null, ProcessingMode.MULTI, 2);
        doNothing().when(directoryValidatorImpl).validate(dirPath);

        List<WordCountDto> expectedWords = List.of(
                new WordCountDto("word1", 3),
                new WordCountDto("word2", 3)
        );
        when(fileProcessor.processFiles(anyList(), eq(3), anySet(), anyList(), eq(2), eq(10)))
                .thenReturn(expectedWords);

        AnalysisResult result = service.performAnalysis(request);

        assertThat(result.wordsCount()).isEqualTo(expectedWords);
        assertThat(result.errors()).isEmpty();
        assertThat(result.processedFiles()).isEqualTo(2);
        assertThat(result.executionTime()).isGreaterThanOrEqualTo(0);
        verify(fileProcessor, times(1)).processFiles(anyList(), eq(3), anySet(), anyList(), eq(2), eq(10));
    }

    /**
     * Проверяет, что если в директории нет .txt файлов, возвращается пустой результат,
     * и процессор файлов вызывается с пустым списком.
     */
    @Test
    void performAnalysis_whenNoTxtFiles_shouldReturnEmptyResult(@TempDir Path tempDir) {
        String dirPath = tempDir.toString();
        RequestDto request = new RequestDto(dirPath, 3, 10, null, null, ProcessingMode.MULTI, 2);
        doNothing().when(directoryValidatorImpl).validate(dirPath);
        when(fileProcessor.processFiles(anyList(), eq(3), anySet(), anyList(), eq(2), eq(10)))
                .thenReturn(List.of());

        AnalysisResult result = service.performAnalysis(request);

        assertThat(result.wordsCount()).isEmpty();
        assertThat(result.errors()).isEmpty();
        assertThat(result.processedFiles()).isZero();
        verify(fileProcessor, times(1)).processFiles(eq(List.of()), eq(3), anySet(), anyList(), eq(2), eq(10));
    }

    /**
     * Проверяет, что файлы, не прошедшие валидацию, не передаются в processFiles,
     * а ошибки добавляются в результат.
     */
    @Test
    void performAnalysis_whenSomeFilesInvalid_shouldSkipThemAndAddErrors(@TempDir Path tempDir) throws Exception {
        Path validFile = tempDir.resolve("valid.txt");
        Path invalidFile = tempDir.resolve("invalid.txt");
        Files.writeString(validFile, "hello world");
        Files.writeString(invalidFile, "content");
        String dirPath = tempDir.toString();

        RequestDto request = new RequestDto(dirPath, 3, 10, null, null, ProcessingMode.MULTI, 2);
        doNothing().when(directoryValidatorImpl).validate(dirPath);
        doNothing().when(fileValidatorImpl).validate(validFile.toString());
        doThrow(new IllegalArgumentException("Invalid file")).when(fileValidatorImpl).validate(invalidFile.toString());

        List<WordCountDto> expectedWords = List.of(new WordCountDto("word1", 1), new WordCountDto("word2", 1));
        when(fileProcessor.processFiles(eq(List.of(validFile)), eq(3), anySet(), anyList(), eq(2), eq(10)))
                .thenReturn(expectedWords);

        AnalysisResult result = service.performAnalysis(request);

        assertThat(result.wordsCount()).isEqualTo(expectedWords);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().getFirst().file()).isEqualTo(invalidFile.toString());
        assertThat(result.errors().getFirst().message()).contains("Invalid file");
        assertThat(result.processedFiles()).isEqualTo(1);
    }

    /**
     * Проверяет, что стоп-слова загружаются через {@link IReader#readWords(String)} только
     * если указан валидный файл.
     */
    @Test
    void performAnalysis_shouldLoadStopWordsWhenFileIsValid(@TempDir Path tempDir) throws Exception {
        Path validFile = tempDir.resolve("file.txt");
        Files.writeString(validFile, "content"); // любой контент
        String stopWordsFile = tempDir.resolve("stopwords.txt").toString();
        Files.writeString(Path.of(stopWordsFile), "the\na\nan");

        RequestDto request = new RequestDto(tempDir.toString(), 3, 10, null, stopWordsFile, ProcessingMode.MULTI, 2);
        doNothing().when(directoryValidatorImpl).validate(tempDir.toString());
        when(ireader.readWords(stopWordsFile)).thenReturn(List.of("the", "a", "an"));
        when(fileValidatorImpl.isValid(anyString())).thenReturn(true);
        when(fileProcessor.processFiles(anyList(), anyInt(), anySet(), anyList(), anyInt(), anyInt()))
                .thenReturn(List.of());

        service.performAnalysis(request);

        verify(ireader, times(1)).readWords(stopWordsFile);
    }

    /**
     * Проверяет, что если файл стоп-слов не указан или невалиден, стоп-слова не загружаются.
     */
    @Test
    void performAnalysis_shouldNotLoadStopWordsWhenFileIsInvalid(@TempDir Path tempDir) throws Exception {
        Path validFile = tempDir.resolve("file.txt");
        Files.writeString(validFile, "content");
        RequestDto request = new RequestDto(tempDir.toString(), 3, 10, null, null, ProcessingMode.MULTI, 2);
        doNothing().when(directoryValidatorImpl).validate(tempDir.toString());

        service.performAnalysis(request);

        verify(ireader, never()).readWords(anyString());
    }
}