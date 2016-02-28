package io.pivio.vcs;

import io.pivio.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VcsReaderTest {

    VcsReader vcsReader;
    SvnReader svnReaderMock;
    GitReader gitReaderMock;
    Configuration configurationMock;


    @Before
    public void setup() {
        vcsReader = new VcsReader();
        svnReaderMock = mock(SvnReader.class);
        gitReaderMock = mock(GitReader.class);
        configurationMock = mock(Configuration.class);
        vcsReader.gitReader = gitReaderMock;
        vcsReader.svnReader = svnReaderMock;
        vcsReader.configuration = configurationMock;
    }

    @Test
    public void testGetVCSRoot() throws Exception {
        String sourceDir = "source";
        String gitRemote = "remote";
        String gitUrl = "gitUrl";
        String svnUrl = "svnUrl";
        when(configurationMock.getParameter(Configuration.SWITCH_SOURCE_DIR)).thenReturn(sourceDir);
        when(configurationMock.getParameter(Configuration.SWITCH_GIT_REMOTE)).thenReturn(gitRemote);
        when(gitReaderMock.getRemoteUrl(sourceDir, gitRemote)).thenReturn(gitUrl);
        when(svnReaderMock.getRemoteUrl(sourceDir)).thenReturn(svnUrl);

        String vcsRoot = vcsReader.getVCSRoot();

        assertThat(vcsRoot).isEqualTo("gitUrlsvnUrl");
    }
}