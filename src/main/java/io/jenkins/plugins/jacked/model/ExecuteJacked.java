package io.jenkins.plugins.jacked.model;

public class ExecuteJacked {
    private int ret;
    private String buildStatus;

    public ExecuteJacked(int ret, String buildStatus) {
        this.ret = ret;
        this.buildStatus = buildStatus;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }
}
