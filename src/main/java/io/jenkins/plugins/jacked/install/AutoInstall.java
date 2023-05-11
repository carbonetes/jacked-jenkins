package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.net.URISyntaxException;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

public class AutoInstall {
    public static void start(Boolean autoInstall, FilePath workspace, EnvVars env, Launcher launcher,
            TaskListener listener, String osName) throws IOException, InterruptedException, URISyntaxException {

        if (Boolean.TRUE.equals(autoInstall)) {
            InstallBinary.installJacked(workspace, launcher, listener, env, osName);
        }
    }
}
