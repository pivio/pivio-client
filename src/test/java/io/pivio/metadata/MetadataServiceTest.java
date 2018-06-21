package io.pivio.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.pivio.Configuration;
import java.io.File;
import java.util.Optional;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

public class MetadataServiceTest {

    public MetadataService metadataService;
    io.pivio.metadata.BuildTool mockedBuildTool;
    MetadataReader mockedMetadataReader;

    @Before
    public void setUp() throws Exception {
        mockedBuildTool = mock(io.pivio.metadata.BuildTool.class);
        mockedMetadataReader = mock(MetadataReader.class);
        Configuration configuration = new Configuration();
        metadataService = new MetadataService(mockedBuildTool, configuration);
    }

    @Test
    public void testReadMetadata() throws Exception {
        metadataService.configuration.setParameter(generateCommandLineWithSourceSwitch());
        Metadata expectedResult = new Metadata();
        when(mockedBuildTool.getMetadataReader(new File("/"))).thenReturn(Optional.of(mockedMetadataReader));
        when(mockedMetadataReader.readMetadata(new File("/"))).thenReturn(expectedResult);

        Optional<Metadata> metadata = metadataService.readMetadata();

        verify(mockedMetadataReader, times(1)).readMetadata(new File("/"));
        assertThat(metadata)
            .isPresent()
            .hasValue(expectedResult);
    }

    private CommandLine generateCommandLineWithSourceSwitch() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-source", "/"};

        Options options = new Options();
        options.addOption(Configuration.SWITCH_SOURCE_DIR, true, "The directory with the checkout files.");

        return parser.parse(options, args);
    }
}
