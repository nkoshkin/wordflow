package io.ylab.wordflow.cli.output.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ylab.wordflow.cli.output.IOutputService;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.InfoDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.dto.WordCountDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link OutputServiceImpl}.
 * Проверяют вывод результатов анализа в консоль и сохранение в JSON-файл,
 * а также обработку ошибок записи в файл (fallback на консоль).
 */
class OutputServiceImplTest {

    private final IOutputService outputService = new OutputServiceImpl();
    private final ByteArrayOutputStream consoleCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(consoleCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Создаёт тестовый {@link ResponseDto} с заданными параметрами.
     *
     * @param top            количество топ слов
     * @param minLength      минимальная длина слова
     * @param threads        количество потоков
     * @param processedFiles количество обработанных файлов
     * @param execTime       время выполнения в миллисекундах
     * @return тестовый ResponseDto
     */
    private ResponseDto createTestResponse(int top, int minLength, int threads, int processedFiles, long execTime) {
        InfoDto info = new InfoDto("./testDir", minLength, top, "multi", threads, processedFiles, execTime);
        List<WordCountDto> words = List.of(
                new WordCountDto("word1", 10),
                new WordCountDto("word2", 5)
        );
        List<ErrorDto> errors = List.of(
                new ErrorDto("error.txt", "Access denied")
        );
        return new ResponseDto(info, words, errors);
    }

    // ==================== returnResponse – вывод в консоль ====================

    /**
     * Проверяет, что при отсутствии выходного файла результаты выводятся в консоль
     * в ожидаемом формате (заголовок, статистика, топ-слова, ошибки).
     */
    @Test
    void returnResponse_whenNoOutputFile_shouldPrintToConsole() {
        // given
        ResponseDto response = createTestResponse(10, 5, 4, 15, 1320L);

        // when
        outputService.returnResponse(response, null);

        // then
        String output = consoleCaptor.toString();
        assertThat(output).contains("Mode: MULTI (4 workers)");
        assertThat(output).contains("Processed 15 files in 1320 ms");
        assertThat(output).contains("Top 10 words (min length = 5):");
        assertThat(output).contains("1. word1 - 10");
        assertThat(output).contains("2. word2 - 5");
        assertThat(output).contains("error.txt - Access denied");
    }

    /**
     * Проверяет, что при пустом пути к выходному файлу также используется вывод в консоль.
     */
    @Test
    void returnResponse_whenOutputFileIsBlank_shouldPrintToConsole() {
        ResponseDto response = createTestResponse(5, 3, 2, 8, 500L);
        outputService.returnResponse(response, "");
        assertThat(consoleCaptor.toString()).contains("Mode: MULTI (2 workers)");
    }

    // ==================== returnResponse – сохранение в JSON файл ====================

    /**
     * Проверяет, что при указании пути к выходному файлу результат корректно сохраняется
     * в JSON-файл, а после десериализации содержимое соответствует исходному ответу.
     */
    @Test
    void returnResponse_whenOutputFileProvided_shouldSaveToJson(@TempDir Path tempDir) throws Exception {
        // given
        ResponseDto original = createTestResponse(10, 3, 2, 5, 123L);
        Path outputPath = tempDir.resolve("result.json");

        // when
        outputService.returnResponse(original, outputPath.toString());

        // then
        assertThat(Files.exists(outputPath)).isTrue();

        // Десериализуем обратно в ResponseDto и сравниваем
        ObjectMapper mapper = new ObjectMapper();
        ResponseDto deserialized = mapper.readValue(outputPath.toFile(), ResponseDto.class);

        assertThat(deserialized.infoDto().directory()).isEqualTo(original.infoDto().directory());
        assertThat(deserialized.infoDto().minLength()).isEqualTo(original.infoDto().minLength());
        assertThat(deserialized.infoDto().top()).isEqualTo(original.infoDto().top());
        assertThat(deserialized.infoDto().mode()).isEqualTo(original.infoDto().mode());
        assertThat(deserialized.infoDto().threads()).isEqualTo(original.infoDto().threads());
        assertThat(deserialized.infoDto().processedFiles()).isEqualTo(original.infoDto().processedFiles());
        assertThat(deserialized.infoDto().executionTimeMs()).isEqualTo(original.infoDto().executionTimeMs());

        assertThat(deserialized.words()).containsExactlyElementsOf(original.words());
        assertThat(deserialized.errors()).containsExactlyElementsOf(original.errors());
    }

    /**
     * Проверяет, что при сохранении в файл создаются все недостающие родительские директории.
     */
    @Test
    void returnResponse_whenOutputFileInNonExistentDirectory_shouldCreateDirectories(@TempDir Path tempDir) throws Exception {
        // given
        ResponseDto response = createTestResponse(10, 3, 2, 5, 123L);
        Path deepPath = tempDir.resolve("sub").resolve("dir").resolve("output.json");

        // when
        outputService.returnResponse(response, deepPath.toString());

        // then
        assertThat(Files.exists(deepPath)).isTrue();
        assertThat(Files.isDirectory(deepPath.getParent())).isTrue();
    }

    // ==================== fallback на консоль при ошибке записи ====================

    /**
     * Проверяет, что при ошибке записи в файл (например, когда указанный путь является директорией,
     * а не файлом) происходит fallback на консоль: результат выводится в консоль,
     * а не сохраняется в файл.
     */
    @Test
    void returnResponse_whenWriteToFileFails_shouldFallbackToConsole(@TempDir Path tempDir) throws Exception {
        // given
        ResponseDto response = createTestResponse(10, 3, 2, 5, 123L);
        // Создаём директорию, а не файл
        Path directory = tempDir.resolve("output_dir");
        Files.createDirectories(directory);
        String invalidFilePath = directory.toString();

        // when
        outputService.returnResponse(response, invalidFilePath);

        // then
        String output = consoleCaptor.toString();
        assertThat(output).contains("Mode: MULTI (2 workers)");
        assertThat(output).contains("Failed to save FILE");
        assertThat(output).contains("Output redirect to console");
        // Убеждаемся, что файл не был создан внутри директории
        assertThat(Files.exists(directory.resolve("output_dir"))).isFalse();
    }
}