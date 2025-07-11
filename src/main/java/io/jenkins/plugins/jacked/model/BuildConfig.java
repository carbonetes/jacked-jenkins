package io.jenkins.plugins.jacked.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildConfig {
    private int ret;
    private String buildStatus;
    private String assessmentSummary;

    public BuildConfig(int ret, String buildStatus, String assessmentSummary) {
        this.ret = ret;
        this.buildStatus = buildStatus;
        this.assessmentSummary = assessmentSummary;
    }
}
