package io.ylab.wordflow.cli.output;

import io.ylab.wordflow.cli.output.impl.OutputServiceImpl;
import io.ylab.wordflow.dto.ResponseDto;

/**
 * Сервис для вывода результатов анализа в CLI-режиме.
 * Предоставляет метод {@link #returnResponse(ResponseDto, String)}, который автоматически
 * выбирает вывод в консоль или сохранение в JSON-файл в зависимости от переданного пути.
 *
 * <p>Реализация должна обрабатывать ошибки записи в файл и при необходимости переключаться
 * на вывод в консоль.</p>
 *
 * @see OutputServiceImpl
 */
public interface IOutputService {

    /**
     * Выводит результат анализа в консоль или сохраняет в JSON-файл.
     *
     * <p>Если {@code outputFile} равен {@code null} или пустой строке,
     * результат выводится в консоль, иначе результат сохраняется в указанный файл.</p>
     *
     * <p>При ошибке сохранения в файл (например, недоступная директория) метод логирует ошибку
     * и автоматически переключается на вывод в консоль.</p>
     *
     * @param response   объект {@link ResponseDto} с результатами анализа
     * @param outputFile путь к файлу для сохранения (может быть {@code null} или пустым)
     */
    void returnResponse(ResponseDto response, String outputFile);
}
