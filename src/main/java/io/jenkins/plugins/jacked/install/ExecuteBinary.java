package io.jenkins.plugins.jacked.install;

import java.io.IOException;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

public class ExecuteBinary {

    public static int ExecuteJacked(String[] cmd, FilePath workspace, Launcher launcher,
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
