package io.jenkins.plugins.jacked.binary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import io.jenkins.cli.shaded.org.apache.commons.io.output.ByteArrayOutputStream;

import io.jenkins.plugins.jacked.model.ExecuteJacked;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;
public class ExecuteBinary {

    public ExecuteJacked executeJacked(String[] cmd, JenkinsConfig jenkinsConfig, JackedConfig jackedConfig) throws InterruptedException, IOException {

        String buildStatus;

        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
        int ret = jenkinsConfig.getLauncher().launch()
                .cmds(cmd)
                .stdout(stdoutStream)
                .stderr(stderrStream)
                .pwd(jenkinsConfig.getWorkspace())
                .join();

        String stdout = new String(stdoutStream.toByteArray(), StandardCharsets.UTF_8);
        String stderr = new String(stderrStream.toByteArray(), StandardCharsets.UTF_8);

        jenkinsConfig.getListener().getLogger().println(stdout);
        jenkinsConfig.getListener().getLogger().println(stderr);

         if (Boolean.FALSE.equals(jackedConfig.getSkipFail()) &&  (stdout.contains("failed") || stderr.contains("failed") || stdout.contains("Error")
                    || stderr.contains("Error"))) {
            buildStatus = "failed";
        } else {
            buildStatus = "success";
        }
        return new ExecuteJacked(ret, buildStatus);
    }

}
