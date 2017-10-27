package io.pivio.metadata;

import java.io.File;
import org.springframework.stereotype.Service;

@Service
public class NpmMetadataReader implements MetadataReader {

    @Override
    public Metadata readMetadata(File buildDirectory) {
        return null;
    }
}
