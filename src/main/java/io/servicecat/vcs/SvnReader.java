package io.servicecat.vcs;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;

import java.io.File;
import java.nio.file.Paths;

@Service
public class SvnReader {

    public String getRemoteUrl(String localPath) {
        try {
            File svnWorkDir = Paths.get(localPath).toAbsolutePath().normalize().toFile();
            SVNStatusClient statusClient = SVNClientManager.newInstance().getStatusClient();
            final SVNStatus status = statusClient.doStatus(svnWorkDir, false);
            return status.getURL().toString();
        } catch (Exception ignored) {
            return "";
        }
    }
}
