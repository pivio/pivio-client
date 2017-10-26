package io.pivio.metadata;

import io.pivio.Configuration;
import io.pivio.Logger;
import java.io.File;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final io.pivio.metadata.BuildTool buildTool;
    final Configuration configuration;
    final Logger log = new Logger();

    @Autowired
    public MetadataService(io.pivio.metadata.BuildTool buildTool, Configuration configuration) {
        this.buildTool = buildTool;
        this.configuration = configuration;
    }

    public Optional<Metadata> readMetadata() {
        String sourceRoot = configuration.getParameter(Configuration.SWITCH_SOURCE_DIR);
        File directory = new File(sourceRoot);
        log.verboseOutput("Looking for source code in directory "+directory.getAbsolutePath()+".", configuration.isVerbose());
        Optional<MetadataReader> metadataReader = buildTool.getMetadataReader(directory);
        if (metadataReader.isPresent()) {
            return Optional.of(metadataReader.get().readMetadata(directory));
        } else {
            return Optional.empty();
        }
    }
}
