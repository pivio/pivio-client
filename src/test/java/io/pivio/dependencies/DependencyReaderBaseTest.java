package io.pivio.dependencies;

import io.pivio.Configuration;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DependencyReaderBaseTest {

    private Configuration configurationMock;
    private GradleDependencyReader gradleLicenseReader;
    private final static String gradleLicensePath = "src/test/resources/dependencies/gradle";

    @Before
    public void setUp() throws Exception {
        configurationMock = mock(Configuration.class);
        gradleLicenseReader = new GradleDependencyReader(configurationMock);
        when(configurationMock.isVerbose()).thenReturn(true);
    }

    @Test
    public void testApplyWhiteList() {
        // <dependency name='org ...
        String[] whiteList = new String[]{"^org.*"};
        configurationMock.WHITELIST = whiteList;
        List<Dependency> dependencies = gradleLicenseReader.readDependencies(gradleLicensePath);
        assertEquals(dependencies.size(), 21);

        for(Dependency dependency : dependencies) {
            for (String regex : whiteList) {
                assertEquals(dependency.name.matches(regex), true);
            }
        }
    }

    @Test
    public void testApplyBlackList() {
        // <dependency name='org ...
        String[] blackList = new String[]{"^org.*"};
        configurationMock.BLACKLIST = blackList;
        List<Dependency> dependencies = gradleLicenseReader.readDependencies(gradleLicensePath);
        assertEquals(dependencies.size(), 20);

        for(Dependency dependency : dependencies) {
            for (String regex : blackList) {
                assertEquals(dependency.name.matches(regex), false);
            }
        }
    }

    @Test
    public void testApplyBlackListAndWhiteList() {
        // discard httpcomponents
        String[] blackList = new String[]{"^org.apache.httpcomponents.*"};
        configurationMock.BLACKLIST = blackList;
        // <dependency name='org ...
        String[] whiteList = new String[]{"^org.*"};
        configurationMock.WHITELIST = whiteList;
        List<Dependency> dependencies = gradleLicenseReader.readDependencies(gradleLicensePath);
        assertEquals(dependencies.size(), 19);

        for(Dependency dependency : dependencies) {
            for (String regex : blackList) {
                assertEquals(dependency.name.matches(regex), false);
            }
        }
    }
}
