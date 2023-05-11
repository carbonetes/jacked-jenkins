package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

public class InstallBinary {

    public static void installJacked(FilePath workspace, Launcher launcher, TaskListener listener, EnvVars env,
            String osName)
            throws InterruptedException, IOException, URISyntaxException {
        // Create a temporary directory inside the workspace to store the downloaded
        // files
        FilePath jackedTmpDir = workspace.child("jackedTmpDir");
        jackedTmpDir.mkdirs();

        // Download the install script
        String installScriptUrl = "https://raw.githubusercontent.com/carbonetes/jacked/main/install.sh";
        FilePath installScript = jackedTmpDir.child("install.sh");
        installScript.copyFrom(new URL(installScriptUrl));

        // Set the destination directory where Jacked will be installed
        FilePath destDir = jackedTmpDir.child("jacked");

        // Launch the install script with custom options using sh
        String[] shCmd = { "bash", "install.sh", "-d", destDir.getRemote() };
        Launcher.ProcStarter procStarter = launcher.launch()
                .cmds(shCmd)
                .envs(env).pwd(jackedTmpDir).stdin(null).stdout(listener).stderr(listener.getLogger());
        int ret = procStarter.start().join();

        // Check if the installation was successful and print a message to the listener
        // log
        if (ret == 0) {
            listener.getLogger().println("Installation succeeded");
            if (osName.toLowerCase().contains("windows")) {
                // Add Jacked to the PATH environment variable
                String executableFolder = destDir.child("bin").getRemote();
                env.override("PATH", executableFolder + ";${PATH}");
            }
        } else {
            listener.getLogger().println("Installation failed - error code: " + ret);
        }
    }

}
