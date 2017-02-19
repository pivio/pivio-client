package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
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
        mockedBuildTool = mock(BuildTool.class);
        mockedDependencyReader = mock(DependencyReader.class);
        Configuration configuration = new Configuration();
        dependenciesReader = new DependenciesReader(mockedBuildTool, configuration);
    }

    @Test
    public void testReadLicenses() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithSourceSwitch());
        ArrayList<Dependency> expectedResult = new ArrayList<>();
        expectedResult.add(new Dependency("test", "testurl", new ArrayList()));
        when(mockedBuildTool.getDependencyReader(new File("/"))).thenReturn(mockedDependencyReader);
        when(mockedDependencyReader.readDependencies("/")).thenReturn(expectedResult);

        List<Dependency> dependencies = dependenciesReader.getDependencies();

        verify(mockedDependencyReader, times(1)).readDependencies("/");
        assertThat(dependencies).isEqualTo(expectedResult);
    }

    @Test
    public void testReadAbsoluteSourceCodeDir() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithSourceAndSourceCodeSwitch());

        String sourceDirectory = dependenciesReader.getSourceDirectory();

        assertThat(sourceDirectory).isEqualTo("/tmp");
    }

    @Test
    public void testReadRelativeSourceCodeDir() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithSourceAndRelativeSourceCodeSwitch());

        String sourceDirectory = dependenciesReader.getSourceDirectory();

        assertThat(sourceDirectory).isEqualTo("/tmp/app/src");
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption(Configuration.SWITCH_SOURCE_DIR, true, "The directory with the checkout files.");
        options.addOption(Configuration.SWITCH_SOURCE_CODE, true, "The directory with the checkout files.");
        return options;
    }

    private CommandLine generateCommandLineWithSourceSwitch() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-source", "/"};
        return parser.parse(getOptions(), args);
    }

    private CommandLine generateCommandLineWithSourceAndSourceCodeSwitch() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-source", "/", "-sourcecode", "/tmp"};
        return parser.parse(getOptions(), args);
    }

    private CommandLine generateCommandLineWithSourceAndRelativeSourceCodeSwitch() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-source", "/tmp", "-sourcecode", "app/src"};
        return parser.parse(getOptions(), args);
    }
}
