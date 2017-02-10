package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuildToolTest {

    private BuildTool buildTool;

    @Before
    public void setUp() throws Exception {
        Configuration configurationMock = mock(Configuration.class);
        Logger loggerMock = mock(Logger.class);
        buildTool = new BuildTool(new GradleDependencyReader(), new MavenParentPomDependencyReader(), new SbtDependencyReader(),
                new ManualDependencyReader(),new Configuration(), loggerMock);
        buildTool.loadBuildConfigs();
        when(configurationMock.isVerbose()).thenReturn(false);

    }

    @Test
    public void testDetectedGradleBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getDependencyReader(new File("src/test/resources/buildtool/gradle"));
        assertThat(buildToolInfo).isInstanceOf(GradleDependencyReader.class);
    }

    @Test
    public void testDetectedMavenBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getDependencyReader(new File("src/test/resources/buildtool/maven"));
        assertThat(buildToolInfo).isInstanceOf(MavenParentPomDependencyReader.class);
    }

    @Test
    public void testDetectedSbtBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getDependencyReader(new File("src/test/resources/buildtool/sbt"));
        assertThat(buildToolInfo).isInstanceOf(SbtDependencyReader.class);
    }

    @Test
    public void testDetectedManualBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getDependencyReader(new File("src/test/resources/buildtool/none"));
        assertThat(buildToolInfo).isInstanceOf(ManualDependencyReader.class);
    }

}
