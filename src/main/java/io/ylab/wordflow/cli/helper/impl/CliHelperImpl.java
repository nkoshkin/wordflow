package io.ylab.wordflow.cli.helper.impl;

import io.ylab.wordflow.cli.helper.IHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Реализация {@link IHelper} для вывода справки в CLI-режиме.
 *
 * <p>Активна только при активном профиле {@code cli}. Выводит справочную информацию
 * в консоль, используя многострочный текст (text block).</p>
 *
 * <p>Справка включает:
 * <ul>
 *   <li>формат запуска приложения</li>
 *   <li>список обязательных параметров</li>
 *   <li>список опциональных параметров</li>
 *   <li>примеры использования</li>
 * </ul>
 * </p>
 *
 * @see IHelper
 */
@Service
@Profile("cli")
public class CliHelperImpl implements IHelper {

    /**
     * {@inheritDoc}
     *
     * <p>Выводит справку</p>
     */
    @Override
    public void help() {
        System.out.println("""
            USAGE:
              java -jar wordflow.jar --dir=<path> --min-length=<int> --top=<int> [OPTIONS]
            
            REQUIRED OPTIONS:
              --dir=<path>               Path directory with files
              --min-length=<int>         Min length word
              --top=<int>                Limit output words

            OPTIONAL OPTIONS:
              --output=<path>            Path JSON file for results
              --stopwords=<path>         Path stopwords file
              --help                     Help message
            
            EXAMPLES:
              java -jar wordflow.jar --dir=./source --min-length=5 --top=10
              java -jar wordflow.jar --dir=./source --min-length=5 --top=10 --output=./result.json --stopwords=./stopwords.txt
            """);
    }
}
