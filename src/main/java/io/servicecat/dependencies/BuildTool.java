package io.servicecat.dependencies;

import io.servicecat.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class BuildTool {

    static final String GRADLE = "build.gradle";
    static final String MAVEN = "pom.xml";
    static final String SBT = "build.sbt";
    private Map<String, DependencyReader> detectableBuildFiles = new HashMap<>();

    @Autowired
    GradleDependencyReader gradleDependencyReader;

    @Autowired
    MavenParentPomDependencyReader mavenParentPomDependencyReader;

    @Autowired
    SbtDependencyReader sbtDependencyReader;

    @Autowired
    ManualDependencyReader manualDependencyReader;

    @Autowired
    Configuration configuration;

    @PostConstruct
    public void loadBuildConfigs() {
        detectableBuildFiles.put(GRADLE, gradleDependencyReader);
        detectableBuildFiles.put(MAVEN, mavenParentPomDependencyReader);
        detectableBuildFiles.put(SBT, sbtDependencyReader);
    }

    public DependencyReader getDependencyReader(File directory) {
        return getBuildToolInfo(directory);
    }

    DependencyReader getBuildToolInfo(File directory) {
        DependencyReader[] result = {manualDependencyReader};
        detectableBuildFiles.forEach((buildToolFile, reader) -> {
            String fileNameForBuildToolConfig = directory.getAbsolutePath() + "/" + buildToolFile;
            if (configuration.isVerbose()) {
                System.out.println("Checking for '"+fileNameForBuildToolConfig+"'.");
            }
            if (new File(fileNameForBuildToolConfig).exists()) {
                if (configuration.isVerbose()) {
                    System.out.println("Found "+fileNameForBuildToolConfig+".");
                }
                result[0] = reader;
            }
        });
        return result[0];
    }
}
