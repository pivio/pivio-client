package io.pivio.dependencies;

import io.pivio.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class MavenParentPomDependencyReaderTest {

    private MavenParentPomDependencyReader reader;
    private Logger loggerMock;

    @Before
    public void setup() {
        loggerMock = mock(Logger.class);
        reader = new MavenParentPomDependencyReader(new MavenDependencyReader(loggerMock), loggerMock);
    }

    @Test
    public void testRemoveDuplicates() throws Exception {
        MavenParentPomDependencyReader reader = new MavenParentPomDependencyReader(new MavenDependencyReader(loggerMock), loggerMock);
        ArrayList<Dependency> dependencies = new ArrayList<>();
        dependencies.add(new Dependency("Demo", "1.0", new ArrayList<>()));
        dependencies.add(new Dependency("Demo", "1.0", new ArrayList<>()));
        dependencies.add(new Dependency("Demo2", "1.0", new ArrayList<>()));
        dependencies.add(new Dependency("Demo2", "1.0", new ArrayList<>()));

        List<Dependency> list = reader.removeDuplicates(dependencies);

        assertThat(list).hasSize(2);
    }

    @Test
    public void testReadMultiDependencies() throws Exception {
        List<Dependency> dependencies = reader.readDependencies("src/test/resources/dependencies/mvn/multimodule");

        assertThat(dependencies).hasSize(3);
    }

    @Test
    public void testNonExistingDependencyFile() throws Exception {
        List<Dependency> dependencies = reader.readDependencies("idonotexist");

        assertThat(dependencies).hasSize(0);
    }
}