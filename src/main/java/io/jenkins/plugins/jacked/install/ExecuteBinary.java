package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import io.jenkins.cli.shaded.org.apache.commons.io.output.ByteArrayOutputStream;

public class ExecuteBinary {

    public static int executeJacked(String[] cmd, FilePath workspace, Launcher launcher,
            TaskListener listener, Boolean skipFail) throws InterruptedException, IOException {

        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
        int ret = launcher.launch()
                .cmds(cmd)
                .stdout(stdoutStream)
                .stderr(stderrStream)
                .pwd(workspace)
                .join();

        String stdout = new String(stdoutStream.toByteArray(), StandardCharsets.UTF_8);
        String stderr = new String(stderrStream.toByteArray(), StandardCharsets.UTF_8);

        listener.getLogger().println(stdout);
        listener.getLogger().println(stderr);

        if (Boolean.FALSE.equals(skipFail)) {
            if (stdout.contains("failed") || stderr.contains("failed")) {
                listener.error("Jacked assessment is 'failed'. See recommendations to fix vulnerabilities.");
                throw new AbortException("Build failed");
            }
        }
        return ret;
    }

}
