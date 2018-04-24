package io.pivio.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.pivio.Configuration;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class BuildToolTest {

    private io.pivio.metadata.BuildTool buildTool;

    @Before
    public void setUp() throws Exception {
        Configuration configurationMock = mock(Configuration.class);

        buildTool = new io.pivio.metadata.BuildTool(new MavenMetadataReader(), new NpmMetadataReader(), configurationMock);

        buildTool.loadBuildConfigs();
        when(configurationMock.isVerbose()).thenReturn(false);

    }

    @Test
    public void testDetectedMavenBuildTool() throws Exception {
        MetadataReader reader = buildTool.getMetadataReader(new File("src/test/resources/buildtool/maven")).get();
        assertThat(reader).isInstanceOf(MavenMetadataReader.class);
    }

    @Test
    public void testDetectedNpmBuildTool() throws Exception {
        MetadataReader reader = buildTool.getMetadataReader(new File("src/test/resources/buildtool/npm")).get();
        assertThat(reader).isInstanceOf(NpmMetadataReader.class);
    }
}
