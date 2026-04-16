package io.ylab.wordflow.core.service;

import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;

public interface IFileAnalysisService {

    AnalysisResult performAnalysis(RequestDto requestDto);
}
