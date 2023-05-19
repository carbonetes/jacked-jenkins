package io.jenkins.plugins.jacked.scan;

import java.util.ArrayList;

import io.jenkins.plugins.jacked.os.CheckOS;
import io.jenkins.plugins.jacked.save.FileFormat;

public class SetArgs {
    private static String JACKED = "jacked";
    private static final String FAILCRITERIA = "--fail-criteria";
    private static final String DIR = "--dir";
    private static final String TAR = "--tar";
    private static final String SBOM = "--sbom";
    private static final String CIMODE = "--ci";
    private static final String FILE = "--file";
    private static final String SKIPDBUPDATE = "--skip-db-update";
    private static final String IGNOREPACKAGENAMES = "--ignore-package-names";
    private static final String IGNOREVULNCVES = "--ignore-vuln-cves";

            
    public static String[] scanTypeArgs(String scanType, String severityType, String scanName,
            Boolean skipDbUpdate, String ignorePackageNames, String ignoreCves) {
        String osName = CheckOS.osName();

        if (Boolean.FALSE.equals(CheckOS.isWindows(osName))) {
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
        // CI Mode Enable
        cmdArgs.add(CIMODE);

        // Skip DB Update Enable
        if (Boolean.TRUE.equals(skipDbUpdate)) {
            cmdArgs.add(SKIPDBUPDATE);
        }

        if (ignorePackageNames != null && (ignorePackageNames.length() > 0)) {
                cmdArgs.add(IGNOREPACKAGENAMES);
                cmdArgs.add(ignorePackageNames);
        }
        if (ignoreCves != null && (ignoreCves.length() > 0)) {
                cmdArgs.add(IGNOREVULNCVES);
                cmdArgs.add(ignoreCves);
        }

        // Save output file
        cmdArgs.add(FILE);
        cmdArgs.add(FileFormat.getFileName());

        return cmdArgs.toArray(new String[cmdArgs.size()]);
    }
}
