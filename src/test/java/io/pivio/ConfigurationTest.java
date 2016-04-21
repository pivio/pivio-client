package io.pivio;

import org.apache.commons.cli.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public class ConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new Configuration();
    }

    @Test
    public void testSourceParameterGivenInCommandLine() throws ParseException {
        String[] args = {"-source", "/home/nothing/test"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String source = configuration.getParameter("source");
        assertThat(source).isEqualTo("/home/nothing/test");
    }

    @Test
    public void testSourceParameterNotGivenInCommandLine() throws ParseException, IOException {
        String[] args = {};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String source = configuration.getParameter("source");
        String expectedResult = new java.io.File( "." ).getCanonicalPath();
        assertThat(source).isEqualTo(expectedResult);
    }

    @Test
    public void testSourceParameterAddedInCommandLineParser() throws ParseException {
        String[] args = {"-source", "xxx"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        assertThat(commandLine.hasOption("source")).isTrue();
    }

    @Test
    public void testGetParameterSource() throws Exception {
        String[] args = {"-source", "xxx"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);
        String option = "source";
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("xxx");
    }

    @Test
    public void testGetParameterGitRemoteSpecified() throws Exception {
        String[] args = {"-gitremote", "demo"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String option = "gitremote";
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("demo");
    }

    @Test
    public void testGetParameterGitRemoteOrigin() throws Exception {
        String[] args = {};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String option = "gitremote";
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("origin");
    }

    @Test
    public void testGetParameterUploadServerDefault() throws Exception {
        String[] args = {};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String option = Configuration.SWITCH_SERVICE_URL;
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("http://localhost:9123");
    }

    @Test
    public void testGetParameterUploadServerSpecified() throws Exception {
        String[] args = {"-"+Configuration.SWITCH_SERVICE_URL, "http://test"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);
        String option = Configuration.SWITCH_SERVICE_URL;
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("http://test");
    }

    @Test
    public void testGetParameterYamlSpecified() throws Exception {
        String[] args = {"-file", "test.yaml"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String option = "file";
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("test.yaml");
    }

    @Test
    public void testGetParameterYamlDefault() throws Exception {
        String[] args = {};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        String option = "file";
        String parameter = configuration.getParameter(option);
        assertThat(parameter).isEqualTo("pivio.yaml");
    }

    @Test
    public void testVerboseParameter() throws Exception {
        String[] args = {"-verbose"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        assertThat(configuration.isVerbose()).isTrue();
    }

    @Test
    public void testYamlPathSourceDir() throws Exception {
        String[] args = {"-source", "/tmp"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        assertThat(configuration.getYamlFilePath()).isEqualTo("/tmp/pivio.yaml");
    }

    @Test
    public void testParameterFileOverwritesParameterSource() throws Exception {
        String[] args = {"-source", "/tmp", "-file", "/var/test.yaml"};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        assertThat(configuration.getYamlFilePath()).isEqualTo("/var/test.yaml");
    }

    @Test
    public void testNoFileOrSourceIYamlFileInCurrentDir() throws Exception {
        String[] args = {};
        CommandLine commandLine = configuration.parseCommandLine(args);
        configuration.setParameter(commandLine);

        assertThat(configuration.getYamlFilePath()).isEqualTo(Paths.get("").toAbsolutePath().toString()+ "/pivio.yaml");
    }
}