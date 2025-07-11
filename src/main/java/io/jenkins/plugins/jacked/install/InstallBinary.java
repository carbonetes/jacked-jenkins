package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.net.URL;

import hudson.FilePath;
import io.jenkins.plugins.jacked.compile.Compile;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class InstallBinary {

    public static void installJacked(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
            throws InterruptedException, IOException {
        // Get the workspace root
        FilePath workspace = jenkinsConfig.getWorkspace();
        // Place the binary directly in the workspace root
        FilePath binaryFile = workspace.child("jacked");

        // Download the Jacked binary
        String binaryUrl = "https://github.com/carbonetes/jacked/releases/download/v1.10.3-ci/jacked";
        binaryFile.copyFrom(new URL(binaryUrl));

        // Make the binary executable (Linux/Mac)
        try {
            binaryFile.chmod(0755);
        } catch (IOException | InterruptedException e) {
            jenkinsConfig.getListener().getLogger().println("Warning: Could not set executable permission on Jacked binary.");
        }

        jenkinsConfig.getListener().getLogger().println("Jacked binary downloaded successfully to: " + binaryFile.getRemote());

        // Optionally, set the path or call further setup
        setPath(jenkinsConfig, jackedConfig, binaryFile.getRemote());
    }

    public static void setPath(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig, String jackedExecutablePath)
            throws IOException, InterruptedException {

        jenkinsConfig.getListener().getLogger().println("Jacked binary path: " + jackedExecutablePath);

        // Optionally, you can update PATH or just use the full path when invoking
        // Here, just log and call compile as in your original logic
        Compile compile = new Compile();
        compile.compileArgs(jenkinsConfig, jackedConfig);
    }
}