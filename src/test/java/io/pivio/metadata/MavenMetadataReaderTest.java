package io.pivio.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class MavenMetadataReaderTest {

    private MavenMetadataReader mavenMetadataReader;
    private File testPomDirectory = new File("src/test/resources/metadata/maven");

    @Before
    public void setUp() throws Exception {
        mavenMetadataReader = new MavenMetadataReader();
    }

    @Test
    public void testReadAllMetadataFields() throws Exception {
        Metadata metadata = mavenMetadataReader.readMetadata(testPomDirectory, "pom.xml");

        assertThat(metadata.version).isEqualTo("1.9.42");
        assertThat(metadata.name).isEqualTo("Sample-Service");
        assertThat(metadata.description).isEqualTo("Provides an awesome REST Service.");
    }

    @Test
    public void testReadOnlyVersion() throws Exception {
        Metadata metadata = mavenMetadataReader.readMetadata(testPomDirectory, "pom-without-name-and-description.xml");

        assertThat(metadata.version).isEqualTo("1.9.42");
        assertThat(metadata.name).isNull();
        assertThat(metadata.description).isNull();
    }

}