package io.jenkins.plugins.jacked.install;

import hudson.AbortException;
import hudson.model.TaskListener;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Go {

    public static void install(JenkinsConfig jenkinsConfig) throws AbortException {
        TaskListener listener = jenkinsConfig.getListener();
        String workspaceDir = jenkinsConfig.getWorkspace().getRemote(); // /home/sairen/.jenkins/workspace/jacked_pipeline
        String goTmpDir = workspaceDir; // Static folder for Go binary

        // Replace spaces with underscores for safe directory handling
        goTmpDir = goTmpDir.replace(" ", "_");

        String goUrl = "https://go.dev/dl/go1.22.4.linux-amd64.tar.gz"; // Uses 1.22.4 Go Version
        String downloadPath = goTmpDir + "/go.tar.gz";
        String extractDir = goTmpDir; // final go path = goTmpDir/go

        try {
            // 1. Ensure the target directory exists
            Path goTmpDirPath = Paths.get(goTmpDir);
            Files.createDirectories(goTmpDirPath); // Create the directory if it doesn't exist

            // 2. Download Go tar.gz
            listener.getLogger().println("Downloading Go...");
            try (InputStream in = new URL(goUrl).openStream()) {
                Files.copy(in, Paths.get(downloadPath));
            }

            // 3. Extract tar.gz
            listener.getLogger().println("Extracting Go...");
            ProcessBuilder extract = new ProcessBuilder("tar", "-C", goTmpDir, "-xzf", downloadPath);
            extract.redirectErrorStream(true);
            Process process = extract.start();
            process.waitFor();

            // 4. Remove the tar.gz to save space
            Files.deleteIfExists(Paths.get(downloadPath));

            // 5. Confirm installation path
            /*
            listener.getLogger().println("Go installed at: " + extractDir);
            listener.getLogger().println("Make sure to use " + extractDir + "/bin/go to run Go commands.");
             */

        } catch (Exception e) {
            listener.getLogger().println("Failed to install Go: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
            throw new AbortException("Build failed");
        }
    }
}
