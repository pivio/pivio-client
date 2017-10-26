package io.pivio;

import io.pivio.dependencies.DependenciesReader;
import io.pivio.dependencies.Dependency;
import io.pivio.metadata.MetadataService;
import io.pivio.vcs.VcsReader;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CollectorTest {

    private Collector collector;

    private Reader readerMock;
    private VcsReader vcsReaderMock;
    private DependenciesReader dependenciesReaderMock;
    private MetadataService metadataServiceMock;
    private Configuration configurationMock;

    @Before
    public void setUp() throws Exception {
        readerMock = mock(Reader.class);
        vcsReaderMock = mock(VcsReader.class);
        dependenciesReaderMock = mock(DependenciesReader.class);
        metadataServiceMock = mock(MetadataService.class);
        configurationMock = mock(Configuration.class);
        collector = new Collector(readerMock, dependenciesReaderMock, metadataServiceMock, vcsReaderMock, configurationMock);

        when(configurationMock.isVerbose()).thenReturn(false);
    }

    @Test
    public void testGatherDocumentData() throws Exception {
        Map<String, Object> result = new HashMap<>();
        when(configurationMock.getYamlFilePath()).thenReturn("/tmp");
        when(readerMock.readYamlFile("/tmp")).thenReturn(result);
        when(dependenciesReaderMock.getDependencies()).thenReturn(new ArrayList<>());
        when(vcsReaderMock.getVCSRoot()).thenReturn("vcsroot");
        when(configurationMock.getParameter(Configuration.SWITCH_USE_THIS_YAML_FILE)).thenReturn("SOMETHING");

        Map<String, Object> actual = collector.gatherSingleFile();

        assertThat(actual).isEqualTo(result);
    }

    @Test
    public void testGatherDocumentDataThrowsException() throws Exception {
        when(readerMock.readYamlFile("/tmp")).thenThrow(new FileNotFoundException());
        when(configurationMock.getYamlFilePath()).thenReturn("/tmp");
        try {
            collector.gatherSingleFile();
            fail("Should have thrown Exception.");
        } catch (PivioFileNotFoundException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void testSpecifyYamlFileDoesNotAddLicensesAndVCS() throws Exception {
        Map<String, Object> result = new HashMap<>();
        when(configurationMock.hasOption(Configuration.SWITCH_USE_THIS_YAML_FILE)).thenReturn(true);
        when(configurationMock.isVerbose()).thenReturn(true);
        when(readerMock.readYamlFile("/tmp")).thenReturn(result);

        Map<String, Object> actual = collector.gatherSingleFile();

        assertThat(actual.keySet().contains(Collector.DEPENDENCIES)).isFalse();
        assertThat(actual.keySet().contains(Collector.VCS)).isFalse();
    }

    @Test
    public void testVCSInfoIsAdded() throws Exception {
        Map<String, Object> result = new HashMap<>();
        when(readerMock.readYamlFile("/tmp")).thenReturn(result);
        when(dependenciesReaderMock.getDependencies()).thenReturn(new ArrayList<>());
        when(vcsReaderMock.getVCSRoot()).thenReturn("vcsroot");
        when(configurationMock.getParameter(Configuration.SWITCH_USE_THIS_YAML_FILE)).thenReturn("SOMETHING");

        Map<String, Object> actual = collector.gatherSingleFile();

        assertThat(actual.get(Collector.VCS)).isEqualTo("vcsroot");
    }

    @Test
    public void testDependenciesIsAdded() throws Exception {
        Map<String, Object> result = new HashMap<>();
        when(readerMock.readYamlFile("/tmp")).thenReturn(result);
        ArrayList<Dependency> expected = new ArrayList<>();
        when(dependenciesReaderMock.getDependencies()).thenReturn(expected);
        when(configurationMock.getParameter(Configuration.SWITCH_USE_THIS_YAML_FILE)).thenReturn("SOMETHING");

        when(vcsReaderMock.getVCSRoot()).thenReturn("vcsroot");

        Map<String, Object> actual = collector.gatherSingleFile();

        assertThat(actual.get(Collector.DEPENDENCIES)).isEqualTo(expected);
    }

    @Test
    public void testReadMultipleFile() throws Exception {
        when(configurationMock.hasOption(Configuration.SWITCH_YAML_DIR)).thenReturn(true);
        when(configurationMock.getParameter(Configuration.SWITCH_YAML_DIR)).thenReturn("src/test/resources/examples");

        List<Map<String, Object>> result = collector.gatherMultipleFiles();

        assertThat(result).hasSize(7);
    }
}