package io.pivio.dependencies;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SbtDependencyReaderTest {

    SbtDependencyReader sbtDependencyReader;

    @Before
    public void setUp() {
        sbtDependencyReader = new SbtDependencyReader();
    }

    @Test
    public void testReadDependencies() throws Exception {
        List<Dependency> dependencies = sbtDependencyReader.readDependencies("src/test/resources/dependencies/sbt/");
        assertThat(dependencies.size()).isEqualTo(20);
    }

    @Test
    public void testReadDependenciesLicense() throws Exception {
        List<Dependency> dependencies = sbtDependencyReader.readDependencies("src/test/resources/dependencies/sbt/");
        assertThat(dependencies.get(0).licences.get(0).name).isEqualTo("Apache");
    }

    @Test
    public void testReadDependenciesPackage() throws Exception {
        List<Dependency> dependencies = sbtDependencyReader.readDependencies("src/test/resources/dependencies/sbt/");
        assertThat(dependencies.get(0).name).isEqualTo("com.github.nscala-time:nscala-time_2.11");
    }

}