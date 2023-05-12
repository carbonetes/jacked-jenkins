package io.jenkins.plugins.jacked.scanType;

import java.util.ArrayList;

import io.jenkins.plugins.jacked.os.CheckOS;
import hudson.model.TaskListener;

public class ScanType {
    private static String JACKED = "jacked";
    private static final String FAILCRITERIA = "--fail-criteria";
    private static final String DIR = "--dir";
    private static final String TAR = "--tar";
    private static final String SBOM = "--sbom";
    private static final String CIMODE = "--ci";

    public static String[] scanTypeArgs(String scanType, String severityType, String scanName, Boolean ciMode) {
        String osName = CheckOS.osName();

        if (!CheckOS.isWindows(osName)) {
            JACKED = "./jackedTmpDir/bin/jacked";
        }

        ArrayList<String> cmdArgs = new ArrayList<String>();

        switch (scanType) {
            case "image":
                // jacked <image> --fail-criteria <severityType>
                String image = scanName;
                cmdArgs.add(JACKED);
                cmdArgs.add(image);
                cmdArgs.add(FAILCRITERIA);
                cmdArgs.add(severityType);
                break;
            case "directory":
                // jacked --dir <path> --fail-criteria <severityType>
                String path = scanName;
                cmdArgs.add(JACKED);
                cmdArgs.add(DIR);
                cmdArgs.add(path);
                cmdArgs.add(FAILCRITERIA);
                cmdArgs.add(severityType);
                break;
            case "tar":
                // jacked --dir <path> --fail-criteria <severityType>
                String tarfile = scanName;
                cmdArgs.add(JACKED);
                cmdArgs.add(TAR);
                cmdArgs.add(tarfile);
                cmdArgs.add(FAILCRITERIA);
                cmdArgs.add(severityType);
                break;
            case "sbom":
                // jacked --dir <path> --fail-criteria <severityType>
                String sbomjson = scanName;
                cmdArgs.add(JACKED);
                cmdArgs.add(SBOM);
                cmdArgs.add(sbomjson);
                cmdArgs.add(FAILCRITERIA);
                cmdArgs.add(severityType);
                break;

            default:
                // jacked <image> --fail-criteria <severityType>
                cmdArgs.add(JACKED);
                cmdArgs.add(scanName);
                cmdArgs.add(FAILCRITERIA);
                cmdArgs.add(severityType);
                break;
        }
        if (Boolean.TRUE.equals(ciMode)) {
            cmdArgs.add(CIMODE);
        }

        String[] args = cmdArgs.toArray(new String[cmdArgs.size()]);
        return args;
    }
}
