package io.pivio;

import io.pivio.upload.Writer;
import org.apache.commons.cli.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Application implements CommandLineRunner {

    @Autowired
    Configuration configuration;

    @Autowired
    Collector collector;

    @Autowired
    Writer writer;

    @Autowired
    Logger log;


    /**
     * This is the main method called by Spring Boot when a class implements Sprint Boot CommandLineRunner.
     * This method is the start point of the client.
     *
     * @param args Arguments of the Spring Boot application.
     */
    @Override
    public void run(String[] args) {
        try {
            CommandLine commandLine = configuration.parseCommandLine(args);
            configuration.setParameter(commandLine);
            if (configuration.hasOption(Configuration.SWITCH_HELP)) {
                configuration.outputHelp();
            } else {
                if (configuration.hasOption(Configuration.SWITCH_YAML_DIR)) {
                    List<Map<String, Object>> files = collector.gatherMultipleFiles();
                    for (Map<String, Object> file : files) {
                        checkForIdElement(file);
                        writer.write(file);
                    }
                } else {
                    Map<String, Object> document = collector.gatherSingleFile();
                    checkForIdElement(document);
                    writer.write(document);
                }
            }
        } catch (PivioFileNotFoundException e) {
            log.output(e.getMessage());
            if (configuration.hasOption(Configuration.SWITCH_PIVIO_FILE_NOT_FOUND_EXIT0)) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        } catch (Exception e) {
            log.output(e.getMessage());
            System.exit(1);
        }
    }

    void checkForIdElement(Map<String, Object> document) throws Exception {
        if (!document.containsKey("id")) {
            StringBuilder content = new StringBuilder();
            document.keySet().forEach(key -> {
                content.append(document.get(key));
            });
            throw new Exception("You need to have an id element in your configuration. (" + content + ")");
        }
    }

}
