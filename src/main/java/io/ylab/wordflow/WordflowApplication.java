package io.ylab.wordflow;

import io.ylab.wordflow.configuration.WordFlowConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WordFlowConfiguration.class)
public class WordflowApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(WordflowApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

}
