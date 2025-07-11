package io.jenkins.plugins.jacked.model;

import hudson.AbortException;
import hudson.model.TaskListener;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildConfig {
    private int ret;
    private String buildStatus;
    private String assessmentSummary;

    public BuildConfig(int ret, String buildStatus, String assessmentSummary, Boolean skipFail, TaskListener listener) throws AbortException {
        this.ret = ret;
        this.buildStatus = buildStatus;
        this.assessmentSummary = assessmentSummary;

        if (buildStatus == "failed" && !skipFail) {
            throw new AbortException("Build Failed.");
        }
    }
}