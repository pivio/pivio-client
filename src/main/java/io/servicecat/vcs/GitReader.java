package io.servicecat.vcs;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Service
public class GitReader {

    public String getRemoteUrl(String localPath, String gitRemoteName) {
        File gitWorkDir = Paths.get(localPath).toAbsolutePath().normalize().toFile();
        try {
            Git git = Git.open(gitWorkDir);
            String remote = git.getRepository().getConfig().getString("remote", gitRemoteName, "url");
            if (remote == null) {
                remote = "";
            }
            return remote;
        } catch (IOException ignored) {
        }
        return "";
    }
}
