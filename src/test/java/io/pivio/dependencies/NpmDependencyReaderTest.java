package io.pivio.dependencies;


import io.pivio.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NpmDependencyReaderTest {

    private NpmDependencyReader npmLicenseReader;

    @Before
    public void setUp() throws Exception {
        npmLicenseReader = new NpmDependencyReader();
    }

    @Test
    public void testReadDependency() throws Exception {
        String licenseFolder = "src/test/resources/dependencies/npm";

        List<Dependency> dependencies = npmLicenseReader.readDependencies(licenseFolder);

        assertThat(dependencies).isNotEmpty();

        assertThat(dependencies.get(0).name).isEqualTo("are-we-there-yet");
        assertThat(dependencies.get(0).version).isEqualTo("1.0.0");

        assertThat(dependencies.get(1).name).isEqualTo("@angularlike/core");
        assertThat(dependencies.get(1).version).isEqualTo("1.0.0");

        assertThat(dependencies.get(0).getLicences().size()).isEqualTo(2);

        assertThat(dependencies.get(1).getLicences().size()).isEqualTo(1);
    }

}