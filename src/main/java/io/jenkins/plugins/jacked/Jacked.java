package io.jenkins.plugins.jacked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.jenkins.plugins.jacked.install.ExecuteBinary;
import io.jenkins.plugins.jacked.install.InstallBinary;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

public class Jacked extends Builder implements SimpleBuildStep {
    private static final String SCAN_TARGET_DEFAULT = "dir:/";
    private static final String REP_NAME_DEFAULT = "jackedReport_${JOB_NAME}_${BUILD_NUMBER}.txt";

    private static final String JACKED = "jacked";

    private String scanDest;
    private String repName;
    private String scanName;
    private String selectedFailCriteria;

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

    public String getScanName() {
        return scanName;
    }

    public void setScanName(String scanName) {
        this.scanName = scanName;
    }

    public String getSelectedFailCriteria() {
        return selectedFailCriteria;
    }

    public void setSelectedFailCriteria(String selectedFailCriteria) {
        this.selectedFailCriteria = selectedFailCriteria;
    }

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public Jacked(String scanDest, String repName, String scanName) {
        this.scanDest = scanDest;
        this.repName = repName;
        this.scanName = scanName;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        // Check if Jacked Binary installed on workspace
        String[] cmd = { JACKED };
        if (ExecuteBinary.ExecuteJacked(cmd, workspace, launcher, listener) == 0) {

            // Install Jacked
            InstallBinary.InstallJacked(workspace, launcher, listener, env);
        }

        // Modify Jacked command with argument

        if (scanName != null && scanName != "") {
            cmd = new String[] { JACKED, scanName, "--fail-criteria", selectedFailCriteria };
            ExecuteBinary.ExecuteJacked(cmd, workspace, launcher, listener);
        } else {
            System.out.println("Please Input Scan File");
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