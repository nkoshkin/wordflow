package io.ylab.wordflow.configuration;

import io.ylab.wordflow.enums.ProcessingMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурация параметров анализа по умолчанию.
 * Загружается из файла {@code application.yml} по префиксу {@code wordflow}.
 *
 * <p>Пример конфигурации:
 * <pre>
 * wordflow:
 *   threads: 2
 *   mode: multi
 * </pre>
 * </p>
 *
 * @param mode режим обработки по умолчанию (SINGLE или MULTI)
 * @param threads количество потоков по умолчанию (если не указано в запросе)
 */
@ConfigurationProperties(prefix = "wordflow")
public record WordFlowConfiguration(
        ProcessingMode mode,
        Integer threads

) {
}
