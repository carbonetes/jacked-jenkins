package io.jenkins.plugins.jacked.scanType;

public class ScanType {
    private static final String JACKED = "jacked";
    private static final String FAILCRITERIA = "--fail-criteria";
    private static final String DIR = "--dir";
    private static final String TAR = "--tar";
    private static final String SBOM = "--sbom";

    public static String[] scanTypeArgs(String scanType, String severityType, String scanName) {
        String[] cmdArgs = new String[5];
        switch (scanType) {
            case "image":
                // jacked <image> --fail-criteria <severityType>
                String image = scanName;
                cmdArgs = new String[4];
                cmdArgs[0] = JACKED;
                cmdArgs[1] = image;
                cmdArgs[2] = FAILCRITERIA;
                cmdArgs[3] = severityType;
                break;
            case "directory":
                // jacked --dir <path> --fail-criteria <severityType>
                String path = scanName;
                cmdArgs[0] = JACKED;
                cmdArgs[1] = DIR;
                cmdArgs[2] = path;
                cmdArgs[3] = FAILCRITERIA;
                cmdArgs[4] = severityType;
                break;
            case "tar":
                // jacked --dir <path> --fail-criteria <severityType>
                String tarfile = scanName;
                cmdArgs[0] = JACKED;
                cmdArgs[1] = TAR;
                cmdArgs[2] = tarfile;
                cmdArgs[3] = FAILCRITERIA;
                cmdArgs[4] = severityType;
                break;
            case "sbom":
                // jacked --dir <path> --fail-criteria <severityType>
                String sbomjson = scanName;
                cmdArgs[0] = JACKED;
                cmdArgs[1] = SBOM;
                cmdArgs[2] = sbomjson;
                cmdArgs[3] = FAILCRITERIA;
                cmdArgs[4] = severityType;
                break;

            default:
                // jacked <image> --fail-criteria <severityType>
                cmdArgs = new String[4];
                cmdArgs[0] = JACKED;
                cmdArgs[1] = scanName;
                cmdArgs[2] = FAILCRITERIA;
                cmdArgs[3] = severityType;
                break;
        }
        return cmdArgs;
    }
}
