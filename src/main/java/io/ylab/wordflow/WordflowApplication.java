package io.ylab.wordflow;

import io.ylab.wordflow.configuration.security.SecurityProperties;
import io.ylab.wordflow.configuration.WordFlowConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({WordFlowConfiguration.class , SecurityProperties.class})
public class WordflowApplication {

	public static void main(String[] args) {

		SpringApplication.run(WordflowApplication.class, args);
	}

}
