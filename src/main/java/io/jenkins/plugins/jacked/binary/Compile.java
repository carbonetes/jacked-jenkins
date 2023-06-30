package io.jenkins.plugins.jacked.binary;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import hudson.AbortException;
import hudson.model.TaskListener;
import io.jenkins.plugins.jacked.model.ExecuteJacked;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;
import io.jenkins.plugins.jacked.scan.SetArgs;
import net.sf.json.JSONObject;

public class Compile {
    /**
     * Compile Arguments for Jacked Vulnerability Scanning.
     * Prepares the flags based on the user-input, compiles properly to be executed for scanning.
     * Inside of the method compiled arguments will be executed and build will failed if vulnerability found based on the fail-criteria input.
     * param jenkinsConfig for Jenkins Configuration tools.
     * param jackedConfig for Jacked Configuration user-inputs.
     * throws InterruptedException when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     * throws IOException signals that an I/O exception of some sort has occurred.
     */
    public void compileArgs(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig)
            throws InterruptedException, IOException {

        SetArgs setArgs = new SetArgs();
        ExecuteBinary executeBinary = new ExecuteBinary();

        if (jackedConfig.getScanName() != null && !jackedConfig.getScanName().equals("")) {
            // Compile arguments based on the user-inputs.
            String[] cmdArgs = setArgs.scanTypeArgs(jackedConfig);
            // Execute compiled arguments for Vulnerability Scanning and returns build status for Jenkins Build.
            ExecuteJacked jackedExecute = executeBinary.executeJacked(cmdArgs, jenkinsConfig, jackedConfig);

            // Set Build Status as content of JSON File.
            String buildStatus = jackedExecute.getBuildStatus();
            setBuildStatusContent(buildStatus, jackedConfig, jackedExecute.getAssessmentSummary());
            // Save JSON File and its content.
            generateJSON(jenkinsConfig, jackedConfig);
            // Determines if build will failed.
            buildFailFilter(buildStatus, jenkinsConfig.getListener());

        } else {
            jenkinsConfig.getListener().getLogger().println("Please input your scan name");
        }
    }
    
    /**
     * Build Fail Filter
     * Determines based on the result / assessment of the Vulnerability Scanner Jacked.
     * Throws AbortException when failed that causes the build to fail.
     * param buildStatus get the status of the current build.
     * param listener handles logs.
     * throws AbortException causes of build fail.
     */
    public void buildFailFilter(String buildStatus, TaskListener listener) throws AbortException {
        if ("failed".equals(buildStatus)) {
            listener.error("Jacked assessment is 'failed'. See recommendations to fix vulnerabilities.");
            throw new AbortException("Build failed");
        }
    }

    // Set Build Status as content of JSON File
    public void setBuildStatusContent(String buildStatus, JackedConfig jackedConfig, String assessmentSummary) {
        String status = "success";
        if (Boolean.FALSE.equals(jackedConfig.getSkipFail())) {
            status = buildStatus;
        }
        Map<String, String> keyValuePair = new HashMap<>();
        keyValuePair.put("buildStatus", status);
        keyValuePair.put("jackedAssessment", status);
        keyValuePair.put("assessmentSummary", assessmentSummary);
        keyValuePair.put("scanType", jackedConfig.getScanType());
        keyValuePair.put("scanName", jackedConfig.getScanName());

        jackedConfig.setContent(keyValuePair);
    }
    
    /**
     * Generate Content and Save a JSON File
     * param jenkinsConfig for Jenkins Configuration tools
     * param jackedConfig for Jacked Configuration user-inputs.
     * throws IOException Signals that an I/O exception of some sort has occurred. 
     */
    public void generateJSON(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig) throws IOException {
        JSONObject json = new JSONObject();

        // Put keyvalue pairs inside the json content.
        for (Map.Entry<String, String> entry : jackedConfig.getContent().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            json.put(key, value);
        }

        // JSON File Saving Setup
        String fileName = "jacked_file.json";
        String filePath = jenkinsConfig.getWorkspace().getRemote() + "/" + fileName;

        try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            fileWriter.write(json.toString());
        } catch (IOException e) {
            // Handle the exception
            jenkinsConfig.getListener().getLogger().println("Failed to save JSON file: " + e.getMessage());
            throw e;
        }


        jenkinsConfig.getEnv().put("JACKED_FILE_PATH", filePath);

    }
}
