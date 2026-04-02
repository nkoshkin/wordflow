package io.ylab.wordflow.dto;

import java.util.List;

public record ResponseDto(
        InfoDto infoDto,
        List<WordCountDto> words,
        List<ErrorDto> errors
) {
}
