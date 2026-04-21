package io.ylab.wordflow.cli.runner;

import io.ylab.wordflow.cli.output.IOutputService;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.cli.service.ITextAnalysis;
import io.ylab.wordflow.cli.arguments.IRequestService;
import io.ylab.wordflow.cli.helper.IHelper;
import io.ylab.wordflow.cli.properties.IPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * CLI-раннер для консольного режима работы приложения.
 * Реализует {@link ApplicationRunner} и запускается после инициализации Spring-контекста.
 *
 * <p>Класс активен только при профиле {@code cli}. Обрабатывает аргументы командной строки,
 * выполняет анализ текстов и выводит результат.</p>
 *
 * <p>Основные шаги:
 * <ol>
 *   <li>Проверка флага {@code --help} – если присутствует, выводится справка через {@link IHelper}.</li>
 *   <li>Парсинг аргументов в {@link RequestDto} через {@link IRequestService}.</li>
 *   <li>Вызов синхронного анализа через {@link ITextAnalysis}.</li>
 *   <li>Вывод результата через {@link IOutputService} (консоль или JSON-файл).</li>
 * </ol>
 * </p>
 *
 * @see ApplicationRunner
 * @see IRequestService
 * @see IHelper
 * @see ITextAnalysis
 * @see IOutputService
 * @see IPropertyService
 */
@Component
@Profile("cli")
public class WordFlowRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(WordFlowRunner.class);

    @Autowired
    IRequestService cliRequestServiceImpl;
    @Autowired
    IHelper helper;
    @Autowired
    ITextAnalysis textAnalysis;
    @Autowired
    IOutputService outputServiceImpl;
    @Autowired
    IPropertyService propertyService;

    /**
     * Основной метод, вызываемый после запуска приложения.
     *
     * <p>Последовательность выполнения:
     * <ul>
     *   <li>Если передан {@code --help} – выводится справка и метод завершается.</li>
     *   <li>Иначе выполняется парсинг аргументов, анализ текстов и вывод результата.</li>
     * </ul>
     * </p>
     *
     * @param args аргументы командной строки, переданные приложению
     */
    @Override
    public void run(ApplicationArguments args) {

        if (propertyService.hasParam("help")){
            helper.help();
            return;
        }
        RequestDto request = cliRequestServiceImpl.parse();
        logger.info("get request comlete");
        ResponseDto response = textAnalysis.analyze(request);
        logger.info("get response comlete");
        outputServiceImpl.returnResponse(response, request.outputFile());

    }
}
