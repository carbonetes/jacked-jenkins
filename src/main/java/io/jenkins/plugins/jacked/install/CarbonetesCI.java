package io.jenkins.plugins.jacked.install;

import java.io.IOException;

import hudson.FilePath;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class CarbonetesCI {

    public static void install(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
            throws InterruptedException, IOException {
        // Get the workspace root
        FilePath workspace = jenkinsConfig.getWorkspace();
        String installDir = workspace.getRemote();

        // install via install.sh
        String[] cmd = {
            "bash",
            "-c",
            "curl -sSfL https://raw.githubusercontent.com/carbonetes/ci/main/install.sh | sh -s -- -d " + installDir
        };

        int exitCode = jenkinsConfig.getLauncher().launch()
            .cmds(cmd)
            // .stdout(jenkinsConfig.getListener())
            // .stderr(jenkinsConfig.getListener().getLogger())
            .pwd(workspace)
            .join();

        if (exitCode != 0) {
            throw new IOException("Failed to install binary. Exit code: " + exitCode);
        }

        FilePath binaryPath = workspace.child("carbonetes-ci");
        try {
            binaryPath.chmod(0755);
        } catch (IOException | InterruptedException e) {
            jenkinsConfig.getListener().getLogger().println("Warning: Could not set executable permission on binary.");
        }

        jenkinsConfig.getListener().getLogger().println(" Binary installed successfully to: " + binaryPath.getRemote());

        setPath(jenkinsConfig, jackedConfig, binaryPath.getRemote());
    }

    public static void setPath(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig, String path)
            throws IOException, InterruptedException {

        jenkinsConfig.getListener().getLogger().println("Binary path: " + path);
    }
}
