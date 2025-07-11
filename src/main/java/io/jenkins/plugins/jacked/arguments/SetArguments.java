package io.jenkins.plugins.jacked.arguments;

import java.nio.file.Paths;
import java.util.ArrayList;

import hudson.AbortException;
import io.jenkins.plugins.jacked.constants.Constants;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class SetArguments {
    // Binary: Carbonetes-CI Command Flags
    // ANALYZER
    private static final String ANALYZER = "--analyzer";
    private static final String INPUT = "--input";
    private static final String JACKED = "jacked";
    private static final String SCANTYPE = "--scan-type";
    private static final String FAILCRITERIA = "--fail-criteria";
    private static final String SKIPFAIL = "--skip-fail";
    // API
    private static final String TOKEN = "--token";
    private static final String PLUGIN = "--plugin-type";
    private static final String ENVIRONMENT = "--environment-type";

    public String[] scanTypeArgs(JackedConfig jackedConfig, JenkinsConfig jenkinsConfig) throws AbortException {
        ArrayList<String> cmdArgs = new ArrayList<>();

        String workspaceDir = jenkinsConfig.getWorkspace().getRemote(); // /home/sairen/.jenkins/workspace/jacked pipeline
        String binaryPath = Paths.get(workspaceDir, "carbonetes-ci").toString();
        String CarbonetesCI = binaryPath;

        String SCANTYPEVALUE = jackedConfig.getScanType() != null ? jackedConfig.getScanType() : "";
        String INPUTVALUE = jackedConfig.getScanName() != null ? jackedConfig.getScanName() : "";
        String TOKENINPUT = jackedConfig.getToken() != null ? jackedConfig.getToken() : "";
        String SEVERITYTYPEINPUT = jackedConfig.getSeverityType() != null ? jackedConfig.getSeverityType() : "";

        // ANALYZER
        cmdArgs.add(CarbonetesCI);
        cmdArgs.add(ANALYZER);
        cmdArgs.add(JACKED);

        cmdArgs.add(INPUT);
        cmdArgs.add(INPUTVALUE);

        cmdArgs.add(SCANTYPE);
        cmdArgs.add(SCANTYPEVALUE);

        cmdArgs.add(FAILCRITERIA);
        cmdArgs.add(SEVERITYTYPEINPUT);

        // API
        cmdArgs.add(TOKEN);
        cmdArgs.add(TOKENINPUT);

        cmdArgs.add(PLUGIN);
        cmdArgs.add("jenkins");

        cmdArgs.add(ENVIRONMENT);
        cmdArgs.add("test");

        if (jackedConfig.getSkipFail()) {
            cmdArgs.add(SKIPFAIL);
        } else {
            jackedConfig.setSkipFail(false);
        }


        return cmdArgs.toArray(new String[0]);
    }
}
