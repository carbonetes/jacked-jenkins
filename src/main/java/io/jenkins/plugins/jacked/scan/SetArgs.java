package io.jenkins.plugins.jacked.scan;

import java.util.ArrayList;

import io.jenkins.plugins.jacked.model.JackedConfig;
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
    private static final String IGNOREVULNCVES = "--ignore-cves";

            
    public String[] scanTypeArgs(JackedConfig jackedConfig) {
        CheckOS checkOS = new CheckOS();
        String osName = checkOS.osName();

        if (Boolean.FALSE.equals(checkOS.isWindows(osName))) {
            JACKED = "./jackedTmpDir/bin/jacked";
        }

        ArrayList<String> cmdArgs = new ArrayList<String>();

        switch (jackedConfig.getScanType()) {
            case "image":
                // jacked <image> --fail-criteria <severityType>
                String image = jackedConfig.getScanName();
                cmdArgs.add(JACKED);
                cmdArgs.add(image);
                break;
            case "directory":
                // jacked --dir <path> --fail-criteria <severityType>
                String path = jackedConfig.getScanName();
                cmdArgs.add(JACKED);
                cmdArgs.add(DIR);
                cmdArgs.add(path);
                break;
            case "tar":
                // jacked --dir <path> --fail-criteria <severityType>
                String tarfile = jackedConfig.getScanName();
                cmdArgs.add(JACKED);
                cmdArgs.add(TAR);
                cmdArgs.add(tarfile);
                break;
            case "sbom":
                // jacked --dir <path> --fail-criteria <severityType>
                String sbomjson = jackedConfig.getScanName();
                cmdArgs.add(JACKED);
                cmdArgs.add(SBOM);
                cmdArgs.add(sbomjson);
                break;

            default:
                // jacked <image> --fail-criteria <severityType>
                cmdArgs.add(JACKED);
                cmdArgs.add(jackedConfig.getScanName());
                break;
        }
        
        // Fail Criteria
        cmdArgs.add(FAILCRITERIA);
        cmdArgs.add(jackedConfig.getSeverityType());

        // CI Mode Enable
        cmdArgs.add(CIMODE);

        // Skip DB Update Enable
        if (Boolean.TRUE.equals(jackedConfig.getSkipDbUpdate())) {
            cmdArgs.add(SKIPDBUPDATE);
        }

        if (jackedConfig.getIgnorePackageNames() != null && (jackedConfig.getIgnorePackageNames().length() > 0)) {
                cmdArgs.add(IGNOREPACKAGENAMES);
                cmdArgs.add(jackedConfig.getIgnorePackageNames());
        }
        if (jackedConfig.getIgnoreCves() != null && (jackedConfig.getIgnoreCves().length() > 0)) {
                cmdArgs.add(IGNOREVULNCVES);
                cmdArgs.add(jackedConfig.getIgnoreCves());
        }

        // Save output file
        cmdArgs.add(FILE);
        cmdArgs.add(FileFormat.getFileName());

        return cmdArgs.toArray(new String[cmdArgs.size()]);
    }
}
