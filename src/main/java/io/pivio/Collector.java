package io.pivio;

import io.pivio.dependencies.DependenciesReader;
import io.pivio.dependencies.Dependency;
import io.pivio.vcs.VcsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
class Collector {

    @Autowired
    Reader reader;

    @Autowired
    DependenciesReader dependenciesReader;

    @Autowired
    VcsReader vcsReader;

    @Autowired
    Configuration configuration;

    @Autowired
    Logger log;

    static final String DEPENDENCIES = "software_dependencies";
    static final String VCS = "vcsroot";

    Map<String, Object> gatherSingleFile() {
        Map<String, Object> document = readFile(configuration.getYamlFilePath());
        if (!configuration.hasOption(Configuration.SWITCH_USE_THIS_YAML_FILE) &&
                !configuration.hasOption(Configuration.SWITCH_YAML_DIR)) {
            List<Dependency> dependencies = dependenciesReader.getDependencies();
            if (!dependencies.isEmpty()) {
                document.put(DEPENDENCIES, dependencies);
            }
            document.put(VCS, vcsReader.getVCSRoot());
        }
        log.verboseOutput("Final result has " + document.size() + " entries.");
        return document;
    }

    private Map<String, Object> readFile(String file) {
        Map<String, Object> document = new HashMap();
        try {
            document = reader.readYamlFile(file);
        } catch (FileNotFoundException fnf) {
            throw new PivioFileNotFoundException("Could not find valid config file. " + fnf.getLocalizedMessage());
        }
        return document;
    }

    List<Map<String, Object>> gatherMultipleFiles() throws FileNotFoundException {
        List<Map<String, Object>> documents = new ArrayList<>();
        String parameter = configuration.getParameter(Configuration.SWITCH_YAML_DIR);
        try {
            Files.list(new File(parameter).toPath())
                    .filter(p -> p.getFileName().toString().endsWith(".yaml"))
                    .forEach(yaml -> {
                        log.output("Reading file: " + yaml);
                        documents.add(readFile(yaml.toString()));
                    });
        } catch (Exception e) {
            log.output(e.getMessage());
        }
        return documents;
    }

}