package io.jenkins.plugins.jacked.install;

import java.io.IOException;

import hudson.Launcher;
import io.jenkins.plugins.jacked.compile.Compile;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class Scoop {

    // Check Scoop
    public void checkScoop(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig) throws IOException, InterruptedException {

            String[] checkScoop = { "powershell.exe", "scoop -v" };
            Launcher.ProcStarter checkScoopProcStarter = jenkinsConfig.getLauncher().launch()
                            .cmds(checkScoop)
                            .envs(jenkinsConfig.getEnv())
                            .pwd(jenkinsConfig.getWorkspace())
                            .stdin(null).stdout(jenkinsConfig.getListener())
                            .stderr(jenkinsConfig.getListener().getLogger());

            int checkScoopRet = checkScoopProcStarter.start().join();
        
            if (checkScoopRet != 0) {
                    jenkinsConfig.getListener().getLogger().println("Scoop is not installed.");
                    jenkinsConfig.getListener().getLogger().println("Preparing to install scoop...");
                    installScoop(jenkinsConfig, jackedConfig);
            } else {
                    jenkinsConfig.getListener().getLogger().println("Preparing to install jacked via scoop...");
                    installJacked(jenkinsConfig, jackedConfig);
            }
    }

    // Scoop Install
    public void installScoop(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
                    throws IOException, InterruptedException {

            jenkinsConfig.getListener().getLogger().println("Installing Scoop...");
            Process process = new ProcessBuilder("powershell.exe",
                            "Set-ExecutionPolicy RemoteSigned -scope CurrentUser; iwr -useb get.scoop.sh | iex")
                            .redirectErrorStream(true)
                            .start();
            process.waitFor();
            jenkinsConfig.getListener().getLogger().println("Scoop installation finished");
            installJacked(jenkinsConfig, jackedConfig);

    }

    // Scoop Install Jacked
    public void installJacked(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
                    throws IOException, InterruptedException {

            // Clean up jacked bucket
            String[] bucket = { "powershell", "scoop bucket rm jacked" };
            procCommand(bucket, jenkinsConfig);
            String[] app = { "powershell", "scoop uninstall jacked" };
            procCommand(app, jenkinsConfig);

            jenkinsConfig.getListener().getLogger().println("Preparing to install jacked via scoop...");

            // Add Jacked bucket to Scoop
            String[] addBucket = { "powershell",
                            "scoop bucket add jacked https://github.com/carbonetes/jacked-bucket" };
            procCommand(addBucket, jenkinsConfig);
            // Install/Update Jacked using Scoop
            String[] installJacked = { "powershell", "scoop install jacked" };
            procCommand(installJacked, jenkinsConfig);

            jenkinsConfig.getListener().getLogger().println("Jacked Installed Successfully");

            // Start compiling arguments for scanning
            Compile compile = new Compile();
            compile.compileArgs(jenkinsConfig, jackedConfig);

    }

    public void procCommand(String[] args, JenkinsConfig jenkinsConfig) throws IOException, InterruptedException {

            Launcher.ProcStarter proc = jenkinsConfig.getLauncher().launch()
                            .cmds(args)
                            .envs(jenkinsConfig.getEnv())
                            .pwd(jenkinsConfig.getWorkspace())
                            .stdin(null)
                            .stdout(jenkinsConfig.getListener())
                            .stderr(jenkinsConfig.getListener().getLogger());
            int procCode = proc.join();
            if (procCode != 0) {
                    jenkinsConfig.getListener().getLogger().println("Failed to execute command - error code: " + procCode);
            }
    }

}
