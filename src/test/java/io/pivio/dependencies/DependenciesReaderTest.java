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
    public void testReadLicensesCombined() throws Exception {
        ArrayList<Dependency> resultFe = new ArrayList<>();
        resultFe.add(new Dependency("fe_dependency", "testurl", new ArrayList()));
        ArrayList<Dependency> resultBe = new ArrayList<>();
        resultBe.add(new Dependency("be_dependency_1", "testurl", new ArrayList()));
        resultBe.add(new Dependency("be_dependency_2", "testurl", new ArrayList()));
        dependenciesReader.configuration.setParameter(generateCommandLineWithMultipleSourcesAndRelativeSourceCodeSwitch());
        when(mockedBuildTool.getDependencyReader(new File("/tmp/frontend/src"))).thenReturn(mockedDependencyReader);
        when(mockedDependencyReader.readDependencies("/tmp/frontend/src")).thenReturn(resultFe);
        when(mockedBuildTool.getDependencyReader(new File("/tmp/backend/src"))).thenReturn(mockedDependencyReader);
        when(mockedDependencyReader.readDependencies("/tmp/backend/src")).thenReturn(resultBe);

        List<Dependency> dependencies = dependenciesReader.getDependencies();

        ArrayList<Dependency> expectedResult = new ArrayList<>();
        expectedResult.addAll(resultFe);
        expectedResult.addAll(resultBe);

        verify(mockedDependencyReader, times(2)).readDependencies(anyString());
        assertThat(dependencies).isEqualTo(expectedResult);
    }

    @Test
    public void testReadAbsoluteSourceCodeDir() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithSourceAndSourceCodeSwitch());

        List<String> sourceDirectories = dependenciesReader.getSourceDirectories();

        assertThat(sourceDirectories).isNotEmpty();
        assertThat(sourceDirectories.get(0)).isEqualTo("/tmp");
    }

    @Test
    public void testReadRelativeSourceCodeDir() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithSourceAndRelativeSourceCodeSwitch());

        List<String> sourceDirectories = dependenciesReader.getSourceDirectories();

        assertThat(sourceDirectories).isNotEmpty();
        assertThat(sourceDirectories.get(0)).isEqualTo("/tmp/app/src");
    }

    @Test
    public void testMultipleAbsoluteSourceCodeDirs() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithMultipleSources());

        List<String> sourceDirectories = dependenciesReader.getSourceDirectories();

        assertThat(sourceDirectories).isNotEmpty();
        assertThat(sourceDirectories.get(0)).isEqualTo("/abs/frontend/src");
        assertThat(sourceDirectories.get(1)).isEqualTo("/abs/backend/src");
    }

    @Test
    public void testMultipleRelativeSourceCodeDirs() throws Exception {
        dependenciesReader.configuration.setParameter(generateCommandLineWithMultipleSourcesAndRelativeSourceCodeSwitch());

        List<String> sourceDirectories = dependenciesReader.getSourceDirectories();

        assertThat(sourceDirectories).isNotEmpty();
        assertThat(sourceDirectories.get(0)).isEqualTo("/tmp/frontend/src");
        assertThat(sourceDirectories.get(1)).isEqualTo("/tmp/backend/src");
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

    private CommandLine generateCommandLineWithMultipleSources() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-sourcecode", "/abs/frontend/src,/abs/backend/src"};
        return parser.parse(getOptions(), args);
    }

    private CommandLine generateCommandLineWithMultipleSourcesAndRelativeSourceCodeSwitch() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-source", "/tmp", "-sourcecode", "frontend/src,backend/src"};
        return parser.parse(getOptions(), args);
    }
}
