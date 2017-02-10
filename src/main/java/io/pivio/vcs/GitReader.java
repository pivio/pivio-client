package io.pivio.vcs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
            return git.reflog().call().iterator().next().getWho().getWhen().toString();
        } catch (IOException | GitAPIException ignored) {
            throw new IllegalArgumentException("Could not get last commit date from Git.");
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

}
