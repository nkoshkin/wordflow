package io.ylab.wordflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordflowApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(WordflowApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

}
