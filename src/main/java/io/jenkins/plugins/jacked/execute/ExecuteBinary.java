package io.jenkins.plugins.jacked.execute;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import io.jenkins.cli.shaded.org.apache.commons.io.output.ByteArrayOutputStream;

import io.jenkins.plugins.jacked.model.ExecuteJacked;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class ExecuteBinary {

    public ExecuteJacked executeJacked(String[] cmd, JenkinsConfig jenkinsConfig, JackedConfig jackedConfig) throws InterruptedException, IOException {

        String buildStatus = null;
        String assessmentSummary = null;

        if (cmd == null || cmd.length == 0 || containsNull(cmd)) {
            throw new IllegalArgumentException("Command array is null, empty, or contains null elements.");
        }

        // Log command for debug purposes
        jenkinsConfig.getListener().getLogger().println("Analyzing...");

        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
        int ret = jenkinsConfig.getLauncher().launch()
                .cmds(cmd)
                .stdout(stdoutStream)
                .stderr(stderrStream)
                .pwd(jenkinsConfig.getWorkspace()) // <-- Fixed: use workspace root
                .join();

        String stdout = new String(stdoutStream.toByteArray(), StandardCharsets.UTF_8);
        String stderr = new String(stderrStream.toByteArray(), StandardCharsets.UTF_8);

        jenkinsConfig.getListener().getLogger().println(stdout);
        jenkinsConfig.getListener().getLogger().println(stderr);

        // Extract the line containing 'failed'
        if (ret != 0 || Boolean.FALSE.equals(jackedConfig.getSkipFail()) && 
            (stdout.toLowerCase().contains("failed") || stderr.toLowerCase().contains("failed") ||
            stdout.toLowerCase().contains("error") || stderr.toLowerCase().contains("error") ||
            stdout.toLowerCase().contains("404")   || stderr.toLowerCase().contains("404")   ||
            stdout.toLowerCase().contains("status code") || stderr.toLowerCase().contains("status code"))) {
            
            buildStatus = "failed";

            String[] lines = stdout.split("\\r?\\n");
            for (String line : lines) {
                if (line.toLowerCase().contains("failed") ||
                    line.toLowerCase().contains("404") ||
                    line.toLowerCase().contains("status code")) {
                    assessmentSummary = line.trim();
                    break;
                }
            }

            if (assessmentSummary == null) {
                lines = stderr.split("\\r?\\n");
                for (String line : lines) {
                    if (line.toLowerCase().contains("failed") ||
                        line.toLowerCase().contains("404") ||
                        line.toLowerCase().contains("status code")) {
                        assessmentSummary = line.trim();
                        break;
                    }
                }
            }
        } else {
            buildStatus = "success";
        }

        return new ExecuteJacked(ret, buildStatus, assessmentSummary);
    }

    private boolean containsNull(String[] array) {
        for (String s : array) {
            if (s == null) return true;
        }
        return false;
    }
}