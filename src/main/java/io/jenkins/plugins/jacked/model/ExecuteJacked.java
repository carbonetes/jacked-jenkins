package io.jenkins.plugins.jacked.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteJacked {
    private int ret;
    private String buildStatus;
    private String assessmentSummary;

    public ExecuteJacked(int ret, String buildStatus, String assessmentSummary) {
        this.ret = ret;
        this.buildStatus = buildStatus;
        this.assessmentSummary = assessmentSummary;
    }
}
