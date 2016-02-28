package io.pivio;

import io.pivio.dependencies.DependenciesReader;
import io.pivio.vcs.VcsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
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

    final static String DEPENDENCIES = "dependencies";
    final static String VCS = "vcsroot";

    Map<String, Object> gatherSingleFile() throws IllegalArgumentException {
        Map<String, Object> document = readFile(configuration.getYamlFilePath());
        if (!configuration.hasOption(Configuration.SWITCH_USE_THIS_YAML_FILE) &&
                !configuration.hasOption(Configuration.SWITCH_YAML_DIR)) {
            document.put(DEPENDENCIES, dependenciesReader.getDependencies());
            document.put(VCS, vcsReader.getVCSRoot());
        }
        if (configuration.isVerbose()) {
            System.out.println("Final result has " + document.size() + " entries.");
        }
        return document;
    }

    private Map<String, Object> readFile(String file) {
        Map<String, Object> document = null;
        try {
            document = reader.readYamlFile(file);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find valid config file. " + e.getLocalizedMessage());
        }
        return document;
    }

    List<Map<String, Object>> gatherMultipleFiles() throws IllegalArgumentException {
        List<Map<String, Object>> documents = new ArrayList<>();
        String parameter = configuration.getParameter(Configuration.SWITCH_YAML_DIR);
        try {
            Files.list(new File(parameter).toPath())
                    .filter(p -> p.getFileName().toString().endsWith(".yaml"))
                    .forEach(yaml -> {
                        System.out.println("Reading file: "+yaml);
                        documents.add(readFile(yaml.toString()));
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documents;
    }

}