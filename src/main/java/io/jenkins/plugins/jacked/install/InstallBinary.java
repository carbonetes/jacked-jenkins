package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import hudson.FilePath;
import hudson.Launcher;
import io.jenkins.plugins.jacked.compile.Compile;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class InstallBinary {

    public void installJacked(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
            throws InterruptedException, IOException, URISyntaxException {
        // Create a temporary directory inside the workspace to store the downloaded
        // files
        FilePath jackedTmpDir = jenkinsConfig.getWorkspace().child("jackedTmpDir");
        jackedTmpDir.mkdirs();

        // Download the install script
        String installScriptUrl = "https://raw.githubusercontent.com/carbonetes/jacked/main/install.sh";
        FilePath installScript = jackedTmpDir.child("install.sh");
        installScript.copyFrom(new URL(installScriptUrl));

        // Set the destination directory where Jacked will be installed
        FilePath destDir = jackedTmpDir.child("jacked");

        // Launch the install script with custom options using sh
        String[] shCmd = { "bash", "install.sh", "|", "sh", "-s", "--", "-d", destDir.getRemote() };
        Launcher.ProcStarter procStarter = jenkinsConfig.getLauncher().launch()
                .cmds(shCmd)
                .envs(jenkinsConfig.getEnv())
                .pwd(jackedTmpDir)
                .stdin(null)
                .stdout(jenkinsConfig.getListener())
                .stderr(jenkinsConfig.getListener().getLogger());
        int ret = procStarter.start().join();

        // Check if the installation was successful and print a message to the listener
        if (ret == 0) {
            jenkinsConfig.getListener().getLogger().println("Jacked Installed Successfully");
            setPath(jenkinsConfig, jackedConfig);
        } else {
            jenkinsConfig.getListener().getLogger().println("Installation failed - error code: " + ret);
        }
    }

    public void setPath(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
            throws IOException, InterruptedException {

        FilePath jackedExecutable = jenkinsConfig.getWorkspace().child("jackedTmpDir");
        String jackedExecutablePath = jackedExecutable.getRemote();
        jenkinsConfig.getListener().getLogger().println(jackedExecutablePath);
        String binPath = "/bin";

        String[] cmd = new String[] { "sh", "-c",
                "export PATH=\"" + jackedExecutablePath + binPath + ":$PATH && jacked\"" };
        Launcher.ProcStarter proc = jenkinsConfig.getLauncher().launch()
                .cmds(cmd)
                .envs(jenkinsConfig.getEnv())
                .pwd(jenkinsConfig.getWorkspace().getRemote())
                .stdin(null)
                .stdout(jenkinsConfig.getListener())
                .stderr(jenkinsConfig.getListener().getLogger());
        int procCode = proc.join();
        if (procCode != 0) {
            jenkinsConfig.getListener().getLogger().println("Failed to set PATH environment variable - error code: " + procCode);
        } else {
            jenkinsConfig.getListener().getLogger().println("PATH environment variable has been updated successfully.");

            // Update the compile arguments for scanning from the jackedConfig instance variable
            Compile compile = new Compile();
            compile.compileArgs(jenkinsConfig, jackedConfig);
        }
    }

}
