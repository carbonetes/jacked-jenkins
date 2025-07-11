package io.jenkins.plugins.jacked.compile;

import java.io.IOException;

import hudson.AbortException;
import hudson.model.TaskListener;
import io.jenkins.plugins.jacked.arguments.SetArguments;
import io.jenkins.plugins.jacked.execute.Execute;
import io.jenkins.plugins.jacked.model.BuildConfig;
import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

public class Compile {
    /*
     * Compile Arguments for Jacked Vulnerability Scanning.
     * Prepares the flags based on the user-input, compiles properly to be executed for scanning.
     * Inside of the method compiled arguments will be executed and build will failed if vulnerability found based on the fail-criteria input.
     * @param jenkinsConfig for Jenkins Configuration tools.
     * @param jackedConfig for Jacked Configuration user-inputs.
     * @throws InterruptedException when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     * @throws IOException signals that an I/O exception of some sort has occurred.
     */
    public void compileArgs(JenkinsConfig jenkinsConfig, JackedConfig jackedConfig) throws InterruptedException, IOException {

        TaskListener listener = jenkinsConfig.getListener();
        listener.getLogger().println("Compilling Commands...");

        SetArguments setArguments = new SetArguments();
        Execute execute = new Execute();

        if (jackedConfig.getScanName() != null && !jackedConfig.getScanName().equals("")) {
            // Compile arguments based on the user-inputs.
            String[] cmdArgs = setArguments.scanTypeArgs(jackedConfig, jenkinsConfig);
           

            // Execute compiled arguments for Vulnerability Scanning and returns build status for Jenkins Build.
            
            BuildConfig jackedExecute = execute.binary(cmdArgs, jenkinsConfig, jackedConfig);
            
            String buildStatus = jackedExecute.getBuildStatus();
            // Determines if build will failed.
            buildFailFilter(buildStatus, jenkinsConfig.getListener());
            

        } else {
            jenkinsConfig.getListener().getLogger().println("Please input your scan name");
        }
    }
    
    /*
     * Build Fail Filter
     * Determines based on the result / assessment of the Vulnerability Scanner Jacked.
     * Throws AbortException when failed that causes the build to fail.
     * @param buildStatus get the status of the current build.
     * @param listener handles logs.
     * @throws AbortException causes of build fail.
     */
    public void buildFailFilter(String buildStatus, TaskListener listener) throws AbortException {
        if ("failed".equals(buildStatus)) {
            throw new AbortException("Build Failed");
        }
    }
}
