package io.pivio;

import io.pivio.upload.Writer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Service
public class Application implements CommandLineRunner {

    private final Configuration configuration;
    private final Collector collector;
    private final Writer writer;
    private final Logger log = new Logger();

    @Autowired
    public Application(Configuration configuration, Collector collector, Writer writer) {
        this.configuration = configuration;
        this.collector = collector;
        this.writer = writer;
    }


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
            } else if (configuration.hasOption(Configuration.SWITCH_VERSION)) {
                configuration.outputVersion();
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

        } catch (PivioMissingIdException | PivioYamlParserException e) {
            log.output(e.getMessage());
            System.exit(1);
        } catch (PivioFileNotFoundException | FileNotFoundException e) {
            log.output(e.getMessage());
            if (configuration.hasOption(Configuration.SWITCH_PIVIO_FILE_NOT_FOUND_EXIT0)) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        } catch (ParseException e) {
            log.output("Could not parse the command line parameters.");
            configuration.outputHelp();
            System.exit(1);
        }
    }

    void checkForIdElement(Map<String, Object> document) throws PivioMissingIdException {
        if (!document.containsKey("id")) {
            StringBuilder content = new StringBuilder();
            document.keySet().forEach(key -> content.append(document.get(key)));
            throw new PivioMissingIdException("You need to have an id element in your configuration. (" + content + ")");
        }
    }

}
