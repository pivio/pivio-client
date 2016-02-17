package io.servicecat.vcs;

import io.servicecat.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}