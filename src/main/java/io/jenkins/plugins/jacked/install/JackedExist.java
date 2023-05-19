package io.jenkins.plugins.jacked.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hudson.FilePath;

public class JackedExist {
    private JackedExist() {
        throw new IllegalStateException("Utility class");
    }

    public static Boolean checkIfExists(FilePath workspace) {
        String workspacePath = workspace.getRemote();
        String version = CheckVersion.getVersion();
        String fileName = "jacked" + version + "Exist.txt";
        String fileContent = "Jacked" + version + " installed on this workspace";

        Boolean fileExists = checkFileExists(workspacePath, fileName);
        if (Boolean.FALSE.equals(fileExists)) {
            try {
                String filePath = workspacePath + "/" + fileName;
                try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
                    writer.write(fileContent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }
    }

    private static Boolean checkFileExists(String workspacePath, String fileName) {
        File file = new File(workspacePath, fileName);
        return file.exists() && !file.isDirectory();
    }
     
}
