package io.pivio.vcs;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

@Service
class GitReader {

    String getRemoteUrl(String localPath, String gitRemoteName) {
        File gitWorkDir = Paths.get(localPath).toAbsolutePath().normalize().toFile();
        Git git = null;
        try {
            git = Git.open(gitWorkDir);
            String remote = git.getRepository().getConfig().getString("remote", gitRemoteName, "url");
            if (remote == null) {
                remote = "";
            }
            return remote;
        } catch (IOException ignored) {
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return "";
    }

    String getLastCommitDate(String localPath) {
        File gitWorkDir = Paths.get(localPath).toAbsolutePath().normalize().toFile();
        Git git = null;
        try {
            git = Git.open(gitWorkDir);
            Date date = git.log().call().iterator().next().getCommitterIdent().getWhen();
            return date.toString();
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Could not get last commit date from Git.");
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

}
