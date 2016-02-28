package io.pivio.dependencies;

import io.pivio.Configuration;
import org.apache.commons.cli.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DependenciesReaderTest {

    public DependenciesReader dependenciesReader;
    BuildTool mockedBuildTool;
    DependencyReader mockedDependencyReader;

    @Before
    public void setUp() throws Exception {
        dependenciesReader = new DependenciesReader();
        mockedBuildTool = mock(BuildTool.class);
        mockedDependencyReader = mock(DependencyReader.class);
        dependenciesReader.buildTool = mockedBuildTool;
        dependenciesReader.configuration = new Configuration();
        dependenciesReader.configuration.setParameter(generateCommandLine());
    }

    @Test
    public void testReadLicenses() throws Exception {
        ArrayList<Dependency> expectedResult = new ArrayList<>();
        expectedResult.add(new Dependency("test", "testurl", new ArrayList()));
        when(mockedBuildTool.getDependencyReader(new File("/"))).thenReturn(mockedDependencyReader);
        when(mockedDependencyReader.readDependencies("/")).thenReturn(expectedResult);

        List<Dependency> dependencies = dependenciesReader.getDependencies();

        verify(mockedDependencyReader, times(1)).readDependencies("/");
        assertThat(dependencies).isEqualTo(expectedResult);
    }

    private CommandLine generateCommandLine() throws ParseException {
        Options options = new Options();
        options.addOption(Configuration.SWITCH_SOURCE_DIR, true, "The directory with the checkout files.");
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-source", "/"};
        return parser.parse(options, args);
    }
}
