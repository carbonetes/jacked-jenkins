package io.jenkins.plugins.jacked.install;

import org.eclipse.jgit.api.Git;

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

        // Set the destination directory where Jacked will be installed
        FilePath destDir = jackedTmpDir.child("jacked");

        try {
            // Clean up any existing repo clone
            if (destDir.exists()) {
                listener.getLogger().println("Cleaning up existing Jacked directory...");
                destDir.deleteRecursive();
            }
            listener.getLogger().println("Preparing Dependencies...");

            // Clone to Jenkins job workspace
            Git.cloneRepository()
                .setURI("https://github.com/carbonetes/jacked.git")
                .setDirectory(new File(destDir.getRemote()))
                .setBranch("v1.9.1-ci")
                .call();

            listener.getLogger().println("Repository cloned successfully to: " + destDir.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Error during repository clone: " + e.getMessage());
            e.printStackTrace(listener.getLogger());throw new AbortException("Build failed");
        }
    }
}
