package io.jenkins.plugins.jacked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

public class Jacked extends Builder implements SimpleBuildStep {
    private static final String SCAN_TARGET_DEFAULT = "dir:/";
    private static final String REP_NAME_DEFAULT = "jackedReport_${JOB_NAME}_${BUILD_NUMBER}.txt";

    private String scanDest;
    private String repName;
    private String jackedArg;

    public String getScanDest() {
        return scanDest;
    }

    public void setScanDest(String scanDest) {
        this.scanDest = scanDest;
    }

    public String getRepName() {
        return repName;
    }

    public void setRepName(String repName) {
        this.repName = repName;
    }

    public String getJackedArg() {
        return jackedArg;
    }

    public void setJackedArg(String jackedArg) {
        this.jackedArg = jackedArg;
    }

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public Jacked(String scanDest, String repName, String jackedArg) {
        this.scanDest = scanDest;
        this.repName = repName;
        this.jackedArg = jackedArg;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        // Install Jacked
        String installScriptUrl = "https://raw.githubusercontent.com/carbonetes/jacked/main/install.sh";
        URL url = new URL(installScriptUrl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder script = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            script.append(inputLine).append("\n");
        }
        in.close();

        // Create tmp Directory
        FilePath jackedTmpDir = workspace.child("jackedTmpDir");
        jackedTmpDir.mkdirs();

        // Store Jacked on tmp Directory
        FilePath scriptFile = jackedTmpDir.child("install.sh");
        scriptFile.write(script.toString(), "UTF-8");

        // Launch Install.sh
        int ret = launcher.launch()
                .cmds("sh", scriptFile.getRemote())
                .envs(env)
                .stdout(listener)
                .stderr(listener.getLogger())
                .pwd(workspace)
                .join();

        // Check if the installation was successful
        if (ret == 0) {
            System.out.println("Installation succeeded");
        } else {
            System.out.println("Installation failed - error code: " + ret);
        }

        // Run Jacked

        String jackedCommand = "jacked";
        ret = launcher.launch()
                .cmds(jackedCommand)
                .stdout(listener)
                .stderr(listener.getLogger())
                .pwd(workspace)
                .join();

        // Check if the command ran successfully
        if (ret == 0) {
            System.out.println("Jacked command ran successfully");
        } else {
            System.out.println("Failed to run Jacked command - error code: " + ret);
        }

        // Modify Jacked command with argument

        if (jackedArg != null && jackedArg != "") {
            ret = launcher.launch()
                    .cmds(jackedCommand, jackedArg)
                    .stdout(listener)
                    .stderr(listener.getLogger())
                    .pwd(workspace)
                    .join();

            // Check if the command ran successfully
            if (ret == 0) {
                System.out.println("Jacked with Args command ran successfully");
            } else {
                System.out.println("Failed to run Jacked command with Args - error code: " + ret);
            }
        } else {
            System.out.println("Please Input Arguments");
        }
    }

    @Extension(ordinal = -2)
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            super(Jacked.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        public String getDefaultScanDest() {
            return SCAN_TARGET_DEFAULT;
        }

        @Override
        public String getDisplayName() {
            return "Vulnerability scan with jacked";
        }

        public String getDefaultRepName() {
            return REP_NAME_DEFAULT;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}