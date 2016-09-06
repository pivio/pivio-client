package io.pivio;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReaderTest {

    Reader reader;
    Configuration configurationMock;
    Logger loggerMock;

    @Before
    public void setUp() throws Exception {
        reader = new Reader();
        loggerMock = mock(Logger.class);
        reader.log = loggerMock;
    }

    @Test
    public void testLowerCaseKeys() throws Exception {
        configurationMock = mock(Configuration.class);
        reader.configuration = configurationMock;
        when(configurationMock.isVerbose()).thenReturn(false);

        Map<String, Object> testMap = new HashMap<>();
        Object value = new Object();
        testMap.put("UPPER", value);

        Map<String, Object> result = reader.makeLowerCaseKeys(testMap);

        assertThat(result.get("upper")).isEqualTo(value);
    }

    @Test
    public void testUmlauts() throws Exception {
        Configuration configuration = new Configuration();
        reader.configuration = configuration;
        Options options = new Options();
        options.addOption(Configuration.SWITCH_USE_THIS_YAML_FILE, true, "Not needed.");
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-file", "src/test/resources/testumlauts.yaml"};
        CommandLine commandLine = parser.parse(options, args);
        configuration.setParameter(commandLine);

        Map<String, Object> stringObjectMap = reader.readYamlFile("src/test/resources/testumlauts.yaml");

        ObjectMapper mapper = new ObjectMapper();
        String valueAsString = mapper.writeValueAsString(stringObjectMap);

        Map<String, Object> result = mapper.readValue(valueAsString, Map.class);

        assertThat(result).isEqualTo(stringObjectMap);
    }

    @Test
    public void testFileDoesNotExists() throws Exception {
        assertThatThrownBy(() -> reader.readYamlFile("src/test/resources/nonExistingYamlFile.yaml")).isInstanceOf(FileNotFoundException.class).hasMessageContaining("Could not find src/test/resources/nonExistingYamlFile.yaml.");
    }

    @Test
    public void testNonMapYamlFile() throws Exception {
        assertThatThrownBy(() -> reader.readYamlFile("src/test/resources/NonMapYamlFile.yaml")).isInstanceOf(PivioYamlParserException.class).hasMessageContaining("Data in src/test/resources/NonMapYamlFile.yaml is not valid.");
    }

    @Test
    public void testYamlFile() throws Exception {
        assertThatThrownBy(() -> reader.readYamlFile("src/test/resources/broken-yaml.yaml")).isInstanceOf(PivioYamlParserException.class).hasMessageContaining("Data in src/test/resources/broken-yaml.yaml is not valid");
    }

}