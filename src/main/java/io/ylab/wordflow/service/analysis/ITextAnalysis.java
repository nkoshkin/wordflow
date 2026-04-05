package io.ylab.wordflow.service.analysis;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;


public interface ITextAnalysis {

    ResponseDto analyze(RequestDto requestDto);

}
