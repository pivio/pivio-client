package io.servicecat.dependencies;

import io.servicecat.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManualDependencyReaderTest {


    private ManualDependencyReader manualDependencyReader;

    @Before
    public void setUp() throws Exception {
        manualDependencyReader = new ManualDependencyReader();
    }

    @Test
    public void testReadDependencies() throws Exception {
        List<Dependency> dependencies = manualDependencyReader.readFile(new File("src/test/resources/manual-dependencies.yaml"));

        assertThat(dependencies).hasSize(3);
    }

    @Test
    public void testReadOneDependencyName() throws Exception {
        List<Dependency> dependencies = manualDependencyReader.readFile(new File("src/test/resources/manual-dependencies-one.yaml"));

        assertThat(dependencies.get(0).name).isEqualTo("Strange Framework");
    }

    @Test
    public void testReadOneDependencyLicense() throws Exception {
        List<Dependency> dependencies = manualDependencyReader.readFile(new File("src/test/resources/manual-dependencies-one.yaml"));

        assertThat(dependencies.get(0).licences.get(0).name).isEqualTo("LIC");
    }

    @Test
    public void testDifferentDependenciesName() throws Exception {
        Configuration configurationMock = mock(Configuration.class);
        when(configurationMock.getParameter(Configuration.SWITCH_MANUAL_DEPENDENCIES)).thenReturn("something/ourstuff.yaml");
        manualDependencyReader.configuration = configurationMock;
        List<Dependency> dependencies = manualDependencyReader.readDependencies("src/test/resources/dependencies/manual");

        assertThat(dependencies).hasSize(3);
    }

    @Test
    public void loadNonExistingFile() throws Exception {
        List<Dependency> dependencies = manualDependencyReader.readFile(new File("src/test/nonexisting"));

        assertThat(dependencies).isEmpty();
    }

    @Test
    public void loadNonYamlFile() throws Exception {
        List<Dependency> dependencies = manualDependencyReader.readFile(new File("src/test/resources/non-yaml-file.yaml"));

        assertThat(dependencies).isEmpty();
    }

}