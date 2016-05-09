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

    public BuildTool buildTool;

    Configuration configurationMock;
    Logger loggerMock;

    @Before
    public void setUp() throws Exception {
        buildTool = new BuildTool();
        buildTool.mavenParentPomDependencyReader = new MavenParentPomDependencyReader();
        buildTool.gradleDependencyReader = new GradleDependencyReader();
        buildTool.sbtDependencyReader = new SbtDependencyReader();
        buildTool.manualDependencyReader = new ManualDependencyReader();
        buildTool.loadBuildConfigs();

        configurationMock = mock(Configuration.class);
        buildTool.configuration = configurationMock;
        loggerMock = mock(Logger.class);
        buildTool.log = loggerMock;
        when(configurationMock.isVerbose()).thenReturn(false);

    }

    @Test
    public void testDetectedGradleBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getBuildToolInfo(new File("src/test/resources/buildtool/gradle"));
        assertThat(buildToolInfo).isInstanceOf(GradleDependencyReader.class);
    }

    @Test
    public void testDetectedMavenBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getBuildToolInfo(new File("src/test/resources/buildtool/maven"));
        assertThat(buildToolInfo).isInstanceOf(MavenParentPomDependencyReader.class);
    }

    @Test
    public void testDetectedSbtBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getBuildToolInfo(new File("src/test/resources/buildtool/sbt"));
        assertThat(buildToolInfo).isInstanceOf(SbtDependencyReader.class);
    }

    @Test
    public void testDetectedManualBuildTool() throws Exception {
        DependencyReader buildToolInfo = buildTool.getBuildToolInfo(new File("src/test/resources/buildtool/none"));
        assertThat(buildToolInfo).isInstanceOf(ManualDependencyReader.class);
    }

}
