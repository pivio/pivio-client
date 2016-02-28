package io.pivio;

import io.pivio.upload.Writer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ApplicationTest {

    Application application;
    Configuration configurationMock;
    Collector collectorMock;
    Writer writerMock;


    @Before
    public void setup() {
        application = new Application();
        configurationMock = mock(Configuration.class);
        collectorMock = mock(Collector.class);
        writerMock = mock(Writer.class);
        application.configuration = configurationMock;
        application.collector = collectorMock;
        application.writer = writerMock;
    }


    @Test(expected = Exception.class)
    public void testCheckForIdElement() throws Exception {
        Map<String, Object> document = new HashMap<>();
        application.checkForIdElement(document);
    }

    @Test
    public void testCheckForHelpOutput() throws Exception {
        String[] args = {"-help"};
        when(configurationMock.hasOption(Configuration.SWITCH_HELP)).thenReturn(true);
        when(configurationMock.parseCommandLine(args)).thenReturn(null);
        application.configuration = configurationMock;

        application.run(args);

        verify(configurationMock, times(1)).outputHelp();
    }

    @Test
    public void testCheckGatherData() throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", "demo");
        when(collectorMock.gatherSingleFile()).thenReturn(result);
        String[] args = {"-help"};

        application.run(args);

        verify(collectorMock, times(1)).gatherSingleFile();
        verify(writerMock, times(1)).write(result);
    }

}