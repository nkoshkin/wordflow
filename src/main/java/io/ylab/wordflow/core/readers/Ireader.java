package io.ylab.wordflow.core.readers;

import java.util.List;
import java.util.regex.Pattern;

public interface Ireader {

    Pattern WORD_PATTERN = Pattern.compile("[a-zA-Zа-яА-Я]+");

    List<String> readWords(String resource);
}
