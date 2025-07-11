package io.jenkins.plugins.jacked.arguments;

import java.nio.file.Paths;
import java.util.ArrayList;

import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class SetArguments {
    // Binary: Carbonetes-CI Command Flags
    // ANALYZER
    private static final String ANALYZER = "--analyzer";
    private static final String JACKED = "jacked";
    private static final String SCANTYPE = "--scan-type";
    private static final String FAILCRITERIA = "--fail-criteria";
    // API
    private static final String TOKEN = "--token";
    private static final String PLUGIN = "--plugin-type";
    private static final String ENVIRONMENT = "--environment-type";

    public String[] scanTypeArgs(JackedConfig jackedConfig, JenkinsConfig jenkinsConfig) {
        ArrayList<String> cmdArgs = new ArrayList<>();

        String workspaceDir = jenkinsConfig.getWorkspace().getRemote(); // /home/sairen/.jenkins/workspace/jacked pipeline
        String binaryPath = Paths.get(workspaceDir, "carbonetes-ci").toString();
        String CarbonetesCI = binaryPath;

        cmdArgs.add(CarbonetesCI);
        cmdArgs.add(ANALYZER);
        cmdArgs.add(JACKED);

        String INPUT = jackedConfig.getScanName();

        // Scan type-specific arguments
        switch (jackedConfig.getScanType()) {
            case "image":
                cmdArgs.add(SCANTYPE);
                cmdArgs.add(INPUT);
                break;
            case "directory":
                cmdArgs.add(SCANTYPE);
                cmdArgs.add(INPUT);
                break;
            case "tar":
                cmdArgs.add(SCANTYPE);
                cmdArgs.add(INPUT);
                break;
        }

        cmdArgs.add(FAILCRITERIA);
        cmdArgs.add(jackedConfig.getSeverityType());

        cmdArgs.add(TOKEN);
        cmdArgs.add(jackedConfig.getToken());

        cmdArgs.add(PLUGIN);
        cmdArgs.add("jenkins");

        cmdArgs.add(ENVIRONMENT);
        cmdArgs.add("test");


        return cmdArgs.toArray(new String[0]);
    }
}
