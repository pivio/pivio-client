package io.pivio.metadata;

import io.pivio.Logger;
import java.io.File;
import org.springframework.stereotype.Service;

import static org.joox.JOOX.$;

@Service
public class MavenMetadataReader implements MetadataReader {
    private final Logger log = new Logger();

    @Override
    public Metadata readMetadata(File sourceDirectory) {
        File buildFile = new File(sourceDirectory.getAbsolutePath() + "/" + BuildTool.MAVEN);
        Metadata metadata = new Metadata();
        try {
            metadata.version = $(buildFile).child("version").text();
            metadata.name = $(buildFile).child("name").text();
            metadata.description = $(buildFile).child("description").text();
        } catch (Exception e) {
            log.output("The file " + buildFile.toString() + " could not be read.");
        }
        return metadata;
    }
}
