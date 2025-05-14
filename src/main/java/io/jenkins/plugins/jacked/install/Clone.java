package io.jenkins.plugins.jacked.install;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

import java.io.File;
import java.io.IOException;

public class Clone {
    public static void repo(JenkinsConfig jenkinsConfig) throws IOException, InterruptedException {
        TaskListener listener = jenkinsConfig.getListener();
        FilePath jackedTmpDir = jenkinsConfig.getWorkspace().child("jackedTmpDir");
        jackedTmpDir.mkdirs();
        FilePath destDir = jackedTmpDir.child("jacked");

        try {
            if (destDir.exists()) {
                listener.getLogger().println("Cleaning up existing Jacked directory...");
                destDir.deleteRecursive();
            }

            listener.getLogger().println("Cloning repository and checking out tag v1.9.1-ci...");

            // Clone the repository without checkout
            Git.cloneRepository()
            .setURI("https://github.com/carbonetes/jacked.git")
            .setDirectory(new File(destDir.getRemote()))
            .setBranch("refs/tags/v1.9.1-ci")
            .call();
        

            listener.getLogger().println("Repository cloned and tag v1.9.1-ci checked out at: " + destDir.getRemote());
        } catch (GitAPIException e) {
            listener.getLogger().println("Error during repository clone or checkout: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
            throw new AbortException("Build failed");
        }
    }
}
