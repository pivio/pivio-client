package io.pivio.vcs;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SvnReaderTest {

    @Test
    public void testGetRemoteUrlDoesNotExists() throws Exception {
        SvnReader svnReader = new SvnReader();

        String remoteUrl = svnReader.getRemoteUrl("/doesnotexists");

        assertThat(remoteUrl).isEmpty();
    }
}