package io.ylab.wordflow.cli.helper.impl;

import io.ylab.wordflow.cli.helper.IHelper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link CliHelperImpl}.
 * Проверяют вывод справочной информации в консоль.
 */
class CliHelperImplTest {

    private final IHelper helper = new CliHelperImpl();

    /**
     * Проверяет, что метод {@link CliHelperImpl#help()} выводит в консоль
     * строки, содержащие ключевые элементы справки (USAGE, REQUIRED OPTIONS и т.д.).
     */
    @Test
    void help_shouldPrintUsageInformation() {
        // given
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try {
            helper.help();
        } finally {
            System.setOut(original);
        }

        String output = out.toString();
        assertThat(output).contains("USAGE:");
        assertThat(output).contains("REQUIRED OPTIONS:");
        assertThat(output).contains("--dir=<path>");
        assertThat(output).contains("--min-length=<int>");
        assertThat(output).contains("--top=<int>");
        assertThat(output).contains("--output=<path>");
        assertThat(output).contains("--stopwords=<path>");
        assertThat(output).contains("EXAMPLES:");
    }
}