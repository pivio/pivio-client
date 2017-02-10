package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
class BuildTool {

    static final String GRADLE = "build.gradle";
    static final String MAVEN = "pom.xml";
    static final String SBT = "build.sbt";
    private Map<String, DependencyReader> detectableBuildFiles = new HashMap<>();

    final GradleDependencyReader gradleDependencyReader;
    final MavenParentPomDependencyReader mavenParentPomDependencyReader;
    final SbtDependencyReader sbtDependencyReader;
    final ManualDependencyReader manualDependencyReader;
    final Configuration configuration;
    final Logger log;

    @Autowired
    public BuildTool(GradleDependencyReader gradleDependencyReader, MavenParentPomDependencyReader mavenParentPomDependencyReader, SbtDependencyReader sbtDependencyReader, ManualDependencyReader manualDependencyReader, Configuration configuration, Logger log) {
        this.gradleDependencyReader = gradleDependencyReader;
        this.mavenParentPomDependencyReader = mavenParentPomDependencyReader;
        this.sbtDependencyReader = sbtDependencyReader;
        this.manualDependencyReader = manualDependencyReader;
        this.configuration = configuration;
        this.log = log;
    }

    @PostConstruct
    public void loadBuildConfigs() {
        detectableBuildFiles.put(GRADLE, gradleDependencyReader);
        detectableBuildFiles.put(MAVEN, mavenParentPomDependencyReader);
        detectableBuildFiles.put(SBT, sbtDependencyReader);
    }

    DependencyReader getDependencyReader(File directory) {
        DependencyReader[] result = {manualDependencyReader};
        detectableBuildFiles.forEach((buildToolFile, reader) -> {
            String fileNameForBuildToolConfig = directory.getAbsolutePath() + "/" + buildToolFile;
            log.verboseOutput("Checking for '"+fileNameForBuildToolConfig+"'.");
            if (new File(fileNameForBuildToolConfig).exists()) {
                log.verboseOutput("Found "+fileNameForBuildToolConfig+".");
                result[0] = reader;
            }
        });
        return result[0];
    }
}
