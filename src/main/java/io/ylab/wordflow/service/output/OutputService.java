package io.ylab.wordflow.service.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.dto.WordCountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class OutputService {
    private static final Logger logger = LoggerFactory.getLogger(OutputService.class);



    public void returnResponse(ResponseDto response, String outputFile){
        if (outputFile == null || outputFile.isBlank()){
            logger.info("Print result to console");
            outputToConsole(response);
        }
        else {
            logger.info("Save result to file: {}", outputFile);
            outputToFile(response, outputFile);
        }
    }

    private void outputToFile(ResponseDto response, String outputFile) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try{
            Path path = Paths.get(outputFile);
            if (path.getParent() != null){
                Files.createDirectories(path.getParent());
            }
            mapper.writeValue(path.toFile(), response);
            logger.info("Result saved to file: {}", outputFile);
        } catch (IOException e) {
            logger.error("Failed to save FILE: {}", e.getMessage());
            logger.info("Output redirect to console");
            outputToConsole(response);
        }

    }

    private void outputToConsole(ResponseDto response) {

//        System.out.println("\nDirectory: " + response.infoDto().directory());
//        System.out.println("Min length: " + response.infoDto().minWordLength());
//        System.out.println("Top count: " + response.infoDto().top());
        System.out.printf("\nMode: %s (%d workers)\n", response.infoDto().mode().toUpperCase(), response.infoDto().threads());
        System.out.printf("Processed %d files in %d ms\n", response.infoDto().processedFiles(), response.infoDto().executionTimeMs());
        System.out.printf("\nTop %d words (min length = %d):\n", response.infoDto().top(), response.infoDto().minWordLength());
        System.out.println("\nTop words: [");
        if (!response.words().isEmpty()){
            int i = 1;
            for (WordCountDto wc : response.words()){
                System.out.printf("%d. %s - %d\n", i++, wc.word(), wc.count());
            }
        }
        System.out.println("]");
        System.out.println("\nerrors: [");
        if (!response.errors().isEmpty()){
            int i = 1;
            for (ErrorDto error : response.errors()){
                System.out.printf("%d. %s - %s\n", i++, error.file(), error.message());
            }
        }
        System.out.println("]");


    }
}
