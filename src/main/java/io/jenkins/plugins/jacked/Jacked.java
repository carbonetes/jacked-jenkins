package io.jenkins.plugins.jacked;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.StyledEditorKit.BoldAction;

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
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import io.jenkins.plugins.jacked.install.executeBinary;
import io.jenkins.plugins.jacked.install.installBinary;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

public class Jacked extends Builder implements SimpleBuildStep {
    private static final Logger LOGGER = Logger.getLogger(Jacked.class.getName());

    private static final String SCAN_TARGET_DEFAULT = "dir:/";
    private static final String REP_NAME_DEFAULT = "jackedReport_${JOB_NAME}_${BUILD_NUMBER}.txt";

    private static final String JACKED = "jacked";

    private String scanDest;
    private String repName;
    private String scanName;
    private String selectedSeverityLevel;
    private Boolean autoInstall;

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

    public String getSelectSeverityLevel() {
        return selectedSeverityLevel;
    }

    public void setSelectSeverityLevel(String selectedSeverityLevel) {
        this.selectedSeverityLevel = selectedSeverityLevel;
    }

    public Boolean getAutoInstall() {
        return autoInstall;
    }

    public void setAutoInstall(Boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public Jacked(String scanDest, String repName, String scanName, String selectedSeverityLevel, Boolean autoInstall) {
        this.scanDest = scanDest;
        this.repName = repName;
        this.scanName = scanName;
        this.selectedSeverityLevel = selectedSeverityLevel;
        this.autoInstall = autoInstall;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        // Check if Jacked Binary installed on workspace
        String[] cmd = { JACKED };
        if (executeBinary.executeJacked(cmd, workspace, launcher, listener) == 1 || Boolean.TRUE.equals(autoInstall)) {

            // Install Jacked
            installBinary.installJacked(workspace, launcher, listener, env);
        }

        // Modify Jacked command with argument

        if (scanName != null && scanName != "") {
            String timestampFile = " > jacked" + time() + ".log";
            String[] cmdArgs = { JACKED, scanName, "--fail-criteria", selectedSeverityLevel };
            executeBinary.executeJacked(cmdArgs, workspace, launcher, listener);
        } else {
            System.out.println("Please Input Scan File");
        }
    }

    public static String time() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmm");
        String timestamp = formatter.format(currentDate);

        return timestamp;

    }

    @Extension
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

        public ListBoxModel doFillItems() {
            LOGGER.log(Level.INFO, "doFillItems() called");
            ListBoxModel items = new ListBoxModel(
                    new Option("-- Select --", ""),
                    new Option("Critical", "critical"),
                    new Option("High", "high"),
                    new Option("Medium", "medium"),
                    new Option("Low", "low"),
                    new Option("Negligible", "negligible"),
                    new Option("Unknown", "unknown"));
            LOGGER.log(Level.INFO, "Returning ListBoxModel: {0}", items);
            return items;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }
}