package io.pivio.dependencies;

import io.pivio.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class DependenciesReader {

    @Autowired
    BuildTool buildTool;

    @Autowired
    Configuration configuration;

    public List<Dependency> getDependencies() {
        File directory = new File(configuration.getParameter(Configuration.SWITCH_SOURCE_DIR));
        DependencyReader dependencyReader = buildTool.getDependencyReader(directory);
        return dependencyReader.readDependencies(directory.getAbsolutePath());
    }
}
