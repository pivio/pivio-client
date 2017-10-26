package io.pivio.metadata;

import java.io.File;

public interface MetadataReader {
    Metadata readMetadata(File buildFile);
}
