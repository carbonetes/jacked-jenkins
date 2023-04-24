package io.jenkins.plugins.jacked.install;

import java.io.IOException;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

public class executeBinary {

    public static int executeJacked(String[] cmd, FilePath workspace, Launcher launcher,
            TaskListener listener) throws InterruptedException, IOException {

        int ret = launcher.launch()
                .cmds(cmd)
                .stdout(listener)
                .stderr(listener.getLogger())
                .pwd(workspace)
                .join();
        return ret;
    }
}
