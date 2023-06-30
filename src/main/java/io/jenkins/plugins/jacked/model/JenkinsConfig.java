package io.jenkins.plugins.jacked.model;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import lombok.Getter;

@Getter
public class JenkinsConfig {
    private Run<?, ?> run;
    private FilePath workspace;
    private EnvVars env;
    private Launcher launcher;
    private TaskListener listener;

    public JenkinsConfig(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) {
        this.run = run;
        this.workspace = workspace;
        this.env = env;
        this.launcher = launcher;
        this.listener = listener;
    }
}
