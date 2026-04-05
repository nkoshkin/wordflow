package io.ylab.wordflow.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wordflow")
public record WordFlowConfiguration(
        ProcessingMode mode,
        Integer threads

) {
}
