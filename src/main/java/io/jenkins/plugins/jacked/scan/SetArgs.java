package io.jenkins.plugins.jacked.scan;

import java.nio.file.Paths;
import java.util.ArrayList;

import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;
// import io.jenkins.plugins.jacked.os.CheckOS;
import io.jenkins.plugins.jacked.save.FileFormat;

public class SetArgs {
    private String JACKED = "jacked";
    private static final String FAILCRITERIA = "--fail-criteria";
    private static final String DIR = "--dir";
    private static final String TAR = "--tar";
    private static final String SBOM = "--sbom";
    private static final String CIMODE = "--ci";
    private static final String FILE = "--file";
    private static final String SKIPDBUPDATE = "--skip-db-update";
    private static final String TOKEN = "--token";
    private static final String PLUGIN = "--plugin";

    public String[] scanTypeArgs(JackedConfig jackedConfig, JenkinsConfig jenkinsConfig) {
        ArrayList<String> cmdArgs = new ArrayList<>();

        // Get the Go binary path from the JackedConfigscanTypeArgs(jackedConfig)
        String workspaceDir = jenkinsConfig.getWorkspace().getRemote(); // /home/sairen/.jenkins/workspace/jacked pipeline
        String goTmpDir = Paths.get(workspaceDir, "go").toString(); // Static folder for Go binary

        String goBinaryPath = Paths.get(goTmpDir, "bin", "go").toString();
        String jackedBinaryPath = Paths.get(workspaceDir, "jacked").toString();
        

        // Ensure the Go binary path is valid
        if (goBinaryPath == null || goBinaryPath.isEmpty()) {
            throw new IllegalArgumentException("Go binary path is not set in JackedConfig.");
        }

        /*
        // Add Go binary path to the command args
        cmdArgs.add(goBinaryPath); // Add the dynamic Go path
        cmdArgs.add("run");
        cmdArgs.add(".");
        */
        cmdArgs.add(jackedBinaryPath);
        //cmdArgs.add(JACKED);

        // Scan type-specific arguments
        switch (jackedConfig.getScanType()) {
            case "image":
                cmdArgs.add(jackedConfig.getScanName());
                break;
            case "directory":
                cmdArgs.add(DIR);
                cmdArgs.add(jackedConfig.getScanName());
                break;
            case "tar":
                cmdArgs.add(TAR);
                cmdArgs.add(jackedConfig.getScanName());
                break;
            case "sbom":
                cmdArgs.add(SBOM);
                cmdArgs.add(jackedConfig.getScanName());
                break;
            default:
                cmdArgs.add(jackedConfig.getScanName());
                break;
        }

        // Add standard flags
        cmdArgs.add(CIMODE);
        cmdArgs.add(FAILCRITERIA);
        cmdArgs.add(jackedConfig.getSeverityType());

        cmdArgs.add(TOKEN);
        cmdArgs.add(jackedConfig.getToken());

        cmdArgs.add(PLUGIN);
        cmdArgs.add("jenkins");

        if (Boolean.TRUE.equals(jackedConfig.getSkipDbUpdate())) {
            cmdArgs.add(SKIPDBUPDATE);
        }

        // Output file
        cmdArgs.add(FILE);
        cmdArgs.add(FileFormat.getFileName());

        return cmdArgs.toArray(new String[0]);
    }
}
