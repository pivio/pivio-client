package io.pivio.metadata;

import io.pivio.Configuration;
import io.pivio.Logger;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MetadataBuildTool")
class BuildTool {

    static final String MAVEN = "pom.xml";
    static final String NPM = "package.json";
    private Map<String, MetadataReader> detectableBuildFiles = new HashMap<>();

    private final MavenMetadataReader mavenMetdataReader;
    private final NpmMetadataReader npmMetadataReader;
    private final Configuration configuration;
    private final Logger log = new Logger();

    @Autowired
    public BuildTool(MavenMetadataReader mavenMetadataReader, NpmMetadataReader npmMetadataReader, Configuration configuration) {
        this.mavenMetdataReader = mavenMetadataReader;
        this.npmMetadataReader = npmMetadataReader;
        this.configuration = configuration;
    }

    @PostConstruct
    public void loadBuildConfigs() {
        detectableBuildFiles.put(MAVEN, mavenMetdataReader);
        detectableBuildFiles.put(NPM, npmMetadataReader);
    }

    Optional<MetadataReader> getMetadataReader(File directory) {
        return detectableBuildFiles.entrySet().stream()
            .filter(map -> {
                String buildToolFile = map.getKey();
                String fileNameForBuildToolConfig = directory.getAbsolutePath() + "/" + buildToolFile;
                log.verboseOutput("Checking for '" + fileNameForBuildToolConfig + "'.", configuration.isVerbose());
                if (new File(fileNameForBuildToolConfig).exists()) {
                    log.verboseOutput("Found " + fileNameForBuildToolConfig + ".", configuration.isVerbose());
                    return true;
                } else {
                    return false;
                }
            })
            .map(element -> element.getValue())
            .findAny();
    }
}
