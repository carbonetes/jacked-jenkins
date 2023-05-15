package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import io.jenkins.plugins.jacked.Jacked;

public class InstallBinary {

    public static void installJacked(FilePath workspace, Launcher launcher, TaskListener listener, EnvVars env,
            String scanName, String scanType, String severityType, Boolean ciMode, Boolean skipFail)
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
        String[] shCmd = { "bash", "install.sh", "|", "sh", "-s", "--", "-d", destDir.getRemote() };
        Launcher.ProcStarter procStarter = launcher.launch()
                .cmds(shCmd)
                .envs(env).pwd(jackedTmpDir).stdin(null).stdout(listener).stderr(listener.getLogger());
        int ret = procStarter.start().join();

        // Check if the installation was successful and print a message to the listener
        if (ret == 0) {
            listener.getLogger().println("Jacked Installed Successfully");
            setPath(workspace, launcher, listener, env, scanName, scanType, severityType, ciMode, skipFail);

        } else {
            listener.getLogger().println("Installation failed - error code: " + ret);
        }
    }

    public static void setPath(FilePath workspace, Launcher launcher, TaskListener listener,
            EnvVars env, String scanName, String scanType, String severityType, Boolean ciMode, Boolean skipFail)
            throws IOException, InterruptedException {

        FilePath jackedExecutable = workspace.child("jackedTmpDir");
        String jackedExecutablePath = jackedExecutable.getRemote();
        listener.getLogger().println(jackedExecutablePath);
        // String usrLocalBin = "/usr/local/bin/";
        String binPath = "/bin";

        String[] cmd = new String[] { "sh", "-c",
                "export PATH=\"" + jackedExecutablePath + binPath + ":$PATH && jacked\"" };
        Launcher.ProcStarter proc = launcher.launch()
                .cmds(cmd)
                .envs(env)
                .pwd(workspace.getRemote())
                .stdin(null)
                .stdout(listener)
                .stderr(listener.getLogger());
        int procCode = proc.join();
        if (procCode != 0) {
            listener.getLogger().println("Failed to set PATH environment variable - error code: " + procCode);
        } else {
            listener.getLogger().println("PATH environment variable has been updated successfully.");

            // Compile Arguments for scanning.
            Jacked.compileArgs(workspace, env, launcher, listener, scanName, scanType, severityType, ciMode,
                    skipFail);
        }
    }

}
