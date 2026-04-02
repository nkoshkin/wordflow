package io.ylab.wordflow.service.analysis;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;

import java.util.Optional;

public interface ITextAnalysis {

    Optional<ResponseDto> analyze(RequestDto requestDto);

}
