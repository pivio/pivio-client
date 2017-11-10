package io.pivio.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class NpmMetadataReaderTest {
    private NpmMetadataReader npmMetadataReader;
    private File testPackageJsonDirectory = new File("src/test/resources/metadata/npm");

    @Before
    public void setUp() {
        npmMetadataReader = new NpmMetadataReader();
    }

    @Test
    public void testReadAllMetadataFields() {
        Metadata metadata = npmMetadataReader.readMetadata(testPackageJsonDirectory,"package.json");

        assertThat(metadata.name).isEqualTo("test_application_name");
        assertThat(metadata.description).isEqualTo("This is a test description for the test package.json");
        assertThat(metadata.version).isEqualTo("0.0.1-SNAPSHOT");
    }

    @Test
    public void testReadMandatoryFields() {
        Metadata metadata =  npmMetadataReader.readMetadata(testPackageJsonDirectory,"package.json.no_description");

        assertThat(metadata.name).isEqualTo("test_application_name");
        assertThat(metadata.description).isEqualTo(null);
        assertThat(metadata.version).isEqualTo("0.0.1-SNAPSHOT");
    }
}
