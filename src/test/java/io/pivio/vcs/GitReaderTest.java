package io.pivio.vcs;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class GitReaderTest {

    @Test
    public void testGetRemoteUrlIfNoneExists() throws Exception {
        GitReader gitReader = new GitReader();
        String url = gitReader.getRemoteUrl("/", "demo");

        assertThat(url).isEmpty();
    }
}