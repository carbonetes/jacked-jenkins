package io.jenkins.plugins.jacked.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hudson.FilePath;

public class JackedExist {
    public static Boolean checkIfExists(FilePath workspace) {

        String workspacePath = workspace.getRemote();
        String version = CheckVersion.getVersion();
        String fileName = "jacked" + version + "Exist.txt";
        String fileContent = "Jacked" + version + " installed on this workspace";

        Boolean fileExists = checkFileExists(workspacePath, fileName);
        if (Boolean.FALSE.equals(fileExists)) {
            try {
                String filePath = workspacePath + "/" + fileName;
                FileWriter writer = new FileWriter(filePath);
                writer.write(fileContent);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }
    }

    private static boolean checkFileExists(String workspacePath, String fileName) {
        String filePath = workspacePath + "/" + fileName;
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }
}
