package io.ylab.wordflow.core.service;

import io.ylab.wordflow.core.processor.IFileProcessor;
import io.ylab.wordflow.core.service.impl.FileAnalysisServiceImpl;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;

/**
 * Сервис для выполнения анализа текстов в файловой системе.
 * Содержит бизнес-логику: валидацию директории, сбор файлов, загрузку стоп-слов,
 * обработку через {@link IFileProcessor} и формирование результата.
 *
 * <p>Этот сервис не зависит от базы данных и используется как в REST, так и в CLI.</p>
 *
 * @see FileAnalysisServiceImpl
 * @see AnalysisResult
 */
public interface IFileAnalysisService {

    /**
     * Выполняет полный анализ директории с текстовыми файлами.
     *
     * <p>Последовательность шагов:
     * <ol>
     *   <li>Валидация существования и доступности директории.</li>
     *   <li>Сбор всех файлов с расширением {@code .txt}.</li>
     *   <li>Валидация каждого файла (существует, читаемый, обычный файл).</li>
     *   <li>Загрузка стоп-слов (если указан файл).</li>
     *   <li>Обработка файлов через {@link IFileProcessor} (многопоточная или однопоточная).</li>
     *   <li>Возврат результата с топ-словами, ошибками и статистикой.</li>
     * </ol>
     * </p>
     *
     * @param requestDto параметры анализа (директория, minLength, top, режим, потоки)
     * @return результат анализа {@link AnalysisResult}
     * @throws IllegalArgumentException если директория не существует или недоступна
     */
    AnalysisResult performAnalysis(RequestDto requestDto);
}
