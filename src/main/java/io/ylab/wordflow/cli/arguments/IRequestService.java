package io.ylab.wordflow.cli.arguments;

import io.ylab.wordflow.cli.arguments.impl.CliRequestServiceImpl;
import io.ylab.wordflow.dto.RequestDto;

/**
 * Сервис для парсинга параметров запроса из командной строки (CLI).
 * Предоставляет метод {@link #parse()}, который преобразует аргументы командной строки
 * в объект {@link RequestDto}.
 *
 * <p>Реализация должна обрабатывать все обязательные и опциональные параметры,
 * применять значения по умолчанию и выбрасывать исключение при критических ошибках
 * (например, отсутствие обязательного параметра).</p>
 *
 * @see CliRequestServiceImpl
 */
public interface IRequestService {

    /**
     * Разбирает аргументы командной строки и возвращает DTO запроса.
     * @return объект {@link RequestDto} с заполненными полями
     * @throws IllegalArgumentException если обязательный параметр отсутствует или имеет неверный формат
     */
    RequestDto parse();

}
