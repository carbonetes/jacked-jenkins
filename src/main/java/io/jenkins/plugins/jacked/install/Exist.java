package io.jenkins.plugins.jacked.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hudson.FilePath;

public class Exist {

    public Boolean checkIfExists(FilePath workspace) {
        CheckVersion checkVersion = new CheckVersion();
        String workspacePath = workspace.getRemote();
        String version = checkVersion.getVersion(); 
        String fileName = "carbonetes-ci" + version + "-exist.txt";
        String fileContent = "carbonetes-ci" + version + " installed on this workspace";

        Boolean fileExists = checkFileExists(workspacePath, fileName);
        if (Boolean.FALSE.equals(fileExists)) {
            try {
                String filePath = workspacePath + File.separator + fileName;
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

    private Boolean checkFileExists(String workspacePath, String fileName) {
        File file = new File(workspacePath, fileName);
        return file.exists() && !file.isDirectory();
    }
     
}
