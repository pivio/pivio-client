package io.pivio.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import static org.joox.JOOX.$;

@Service
public class NpmMetadataReader implements MetadataReader {
    private final Logger log = new Logger();

    @Override
    public Metadata readMetadata(File sourceDirectory) {
        Metadata metadata = new Metadata();
        File buildFile = new File(sourceDirectory.getAbsolutePath() + "/" + BuildTool.NPM);
        try {
            Map<String, Object> metadataJson = new ObjectMapper().readerFor(Map.class).readValue(buildFile);
            metadata.version = metadataJson.get("version").toString();
            metadata.name = metadataJson.get("name").toString();
            metadata.description = metadataJson.get("description").toString();
        } catch (Exception e) {
            log.output("The file " + buildFile.toString() + " could not be read.");
        }
        return metadata;
    }
}
