package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DependenciesReader {

    private final BuildTool buildTool;
    final Configuration configuration;
    final Logger log = new Logger();

    @Autowired
    public DependenciesReader(BuildTool buildTool, Configuration configuration) {
        this.buildTool = buildTool;
        this.configuration = configuration;
    }

    public List<Dependency> getDependencies() {
        return getSourceDirectories().stream().flatMap(sourceCodeDirectory -> {
            File directory = new File(sourceCodeDirectory);
            log.verboseOutput("Looking for source code in directory "+directory.getAbsolutePath()+".", configuration.isVerbose());
            DependencyReader dependencyReader = buildTool.getDependencyReader(directory);
            return dependencyReader.readDependencies(directory.getAbsolutePath()).stream();
        })
        .collect(Collectors.toList());
    }

    List<String> getSourceDirectories() {
        String sourceRootParameter = configuration.getParameter(Configuration.SWITCH_SOURCE_DIR);
        List<String> sourceCodesParameter = Arrays.asList(configuration.getParameter(Configuration.SWITCH_SOURCE_CODE).split("[\\s,]+"));

        return sourceCodesParameter.stream().map(sourceCodeParameter -> {
            String sourceCodeDirectory;
            if (sourceCodeParameter.startsWith("/")) {
                sourceCodeDirectory = sourceCodeParameter;
            } else {
                String slash = sourceRootParameter.endsWith("/") ? "" : "/";
                sourceCodeDirectory = sourceRootParameter + slash + sourceCodeParameter;
            }
            return sourceCodeDirectory;
        })
        .collect(Collectors.toList());
    }
}
