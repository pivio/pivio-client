package io.pivio.vcs;

import io.pivio.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class VcsReader {

    @Autowired
    Configuration configuration;

    @Autowired
    GitReader gitReader;

    @Autowired
    SvnReader svnReader;

    public String getVCSRoot() {
        String sourceDir = configuration.getParameter(Configuration.SWITCH_SOURCE_DIR);
        String gitRemote = configuration.getParameter(Configuration.SWITCH_GIT_REMOTE);
        String result = gitReader.getRemoteUrl(sourceDir, gitRemote);
        result = result + svnReader.getRemoteUrl(sourceDir);
        return result;
    }

    public String getLastCommitDate() {
        return gitReader.getLastCommitDate(configuration.getParameter(Configuration.SWITCH_SOURCE_DIR));
    }
}