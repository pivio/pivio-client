package io.pivio.dependencies;

import io.pivio.Logger;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class MavenDependencyReaderTest {

    private MavenDependencyReader mavenLicenseReader;

    @Before
    public void setUp() throws Exception {
        Logger loggerMock = mock(Logger.class);
        mavenLicenseReader = new MavenDependencyReader(loggerMock);
    }

    @Test
    public void testReadDependency() throws Exception {
        String licenseFile = "src/test/resources/mvn-licenses-one.xml";

        List<Dependency> dependencies = mavenLicenseReader.readDependencies(new File(licenseFile));

        assertThat(dependencies.get(0).name).isEqualTo("asm:asm");
    }

    @Test
    public void testReadDependencyWithTwoLicenses() throws Exception {
        String licenseFile = "src/test/resources/mvn-licenses-one.xml";

        List<Dependency> dependencies = mavenLicenseReader.readDependencies(new File(licenseFile));

        Assertions.assertThat(dependencies.get(0).getLicences()).hasSize(2);
    }

    @Test
    public void testReadMultipleDependencies() throws Exception {
        String licenseFile = "src/test/resources/mvn-licenses.xml";

        List<Dependency> dependencies = mavenLicenseReader.readDependencies(new File(licenseFile));

        assertThat(dependencies).hasSize(140);
    }

    @Test
    public void testTryToReadNonExistingFile() throws Exception {
        String licenseFile = "src/nonexisting";

        List<Dependency> dependencies = mavenLicenseReader.readDependencies(new File(licenseFile));

        assertThat(dependencies).hasSize(0);
    }

}