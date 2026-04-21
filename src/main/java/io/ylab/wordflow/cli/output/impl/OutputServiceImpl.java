package io.ylab.wordflow.cli.output.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.ylab.wordflow.cli.output.IOutputService;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.dto.WordCountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Реализация {@link IOutputService} для вывода результатов в CLI-режиме.
 *
 * <p>Активна только при профиле {@code cli}. Обеспечивает вывод в консоль
 * и сохранение результата в JSON-файл с использованием Jackson.</p>
 *
 * <p>Особенности:
 * <ul>
 *   <li>При сохранении в файл автоматически создаются все недостающие родительские директории.</li>
 *   <li>В случае ошибки записи в файл вывод переключается на консоль.</li>
 * </ul>
 * </p>
 *
 * @see IOutputService
 */
@Service
@Profile("cli")
public class OutputServiceImpl implements IOutputService {
    private static final Logger logger = LoggerFactory.getLogger(OutputServiceImpl.class);

    /**
     * {@inheritDoc}
     *
     * <p>Логирует выбранный способ вывода (консоль или файл).</p>
     *
     * @param response   объект {@link ResponseDto} с результатами анализа
     * @param outputFile путь к файлу для сохранения (может быть {@code null} или пустым)
     */
    @Override
    public void returnResponse(ResponseDto response, String outputFile){
        if (outputFile == null || outputFile.isBlank()){
            logger.info("Print result to console");
            outputToConsole(response);
        }
        else {
            logger.info("Save result to file: {}", outputFile);
            outputToFile(response, outputFile);
        }
    }

    /**
     * Сохраняет результат анализа в JSON-файл.
     *
     * <p>Создаёт недостающие директории, использует {@link ObjectMapper} с форматированием
     * (indent output). В случае ошибки логирует её и перенаправляет вывод в консоль.</p>
     *
     * @param response   объект {@link ResponseDto}
     * @param outputFile путь к выходному файлу (не должен быть {@code null} или пустым)
     */
    private void outputToFile(ResponseDto response, String outputFile) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try{
            Path path = Paths.get(outputFile);
            if (path.getParent() != null){
                Files.createDirectories(path.getParent());
            }
            mapper.writeValue(path.toFile(), response);
            logger.info("Result saved to file: {}", outputFile);
        } catch (IOException e) {
            logger.error("Failed to save FILE: {}", e.getMessage());
            logger.info("Output redirect to console");
            outputToConsole(response);
        }

    }

    /**
     * Выводит результат анализа в консоль в форматированном виде.
     *
     * <p>Выводит режим работы, количество потоков, количество обработанных файлов,
     * время выполнения, топ-N слов и список ошибок.</p>
     *
     * @param response объект {@link ResponseDto}
     */
    private void outputToConsole(ResponseDto response) {

        System.out.printf("\nMode: %s (%d workers)\n", response.infoDto().mode().toUpperCase(), response.infoDto().threads());
        System.out.printf("Processed %d files in %d ms\n", response.infoDto().processedFiles(), response.infoDto().executionTimeMs());
        System.out.printf("Top %d words (min length = %d):\n", response.infoDto().top(), response.infoDto().minLength());
        System.out.println("\nTop words: [");
        if (!response.words().isEmpty()){
            int i = 1;
            for (WordCountDto wc : response.words()){
                System.out.printf("%d. %s - %d\n", i++, wc.word(), wc.count());
            }
        }
        System.out.println("]");
        System.out.println("\nerrors: [");
        if (!response.errors().isEmpty()){
            int i = 1;
            for (ErrorDto error : response.errors()){
                System.out.printf("%d. %s - %s\n", i++, error.file(), error.message());
            }
        }
        System.out.println("]");
    }
}
