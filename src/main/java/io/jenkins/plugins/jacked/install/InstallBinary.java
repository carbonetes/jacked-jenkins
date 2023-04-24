package io.jenkins.plugins.jacked.install;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

public class installBinary {

    public static void installJacked(FilePath workspace, Launcher launcher,
            TaskListener listener, EnvVars env) throws InterruptedException, IOException {

        // Install Jacked
        String installScriptUrl = "https://raw.githubusercontent.com/carbonetes/jacked/main/install.sh";
        URL url = new URL(installScriptUrl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder script = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            script.append(inputLine).append("\n");
        }
        in.close();

        // Create tmp Directory
        FilePath jackedTmpDir = workspace.child("jackedTmpDir");
        jackedTmpDir.mkdirs();

        // Store Jacked on tmp Directory
        FilePath scriptFile = jackedTmpDir.child("install.sh");
        scriptFile.write(script.toString(), "UTF-8");

        // Launch Install.sh
        int ret = launcher.launch()
                .cmds("sh", scriptFile.getRemote())
                .envs(env)
                .stdout(listener)
                .stderr(listener.getLogger())
                .pwd(workspace)
                .join();

        // Check if the installation was successful
        if (ret == 0) {
            System.out.println("Installation succeeded");
        } else {
            System.out.println("Installation failed - error code: " + ret);
        }
    }
}
