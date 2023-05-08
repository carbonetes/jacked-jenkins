package io.jenkins.plugins.jacked;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.verb.POST;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.Permission;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import io.jenkins.plugins.jacked.install.ExecuteBinary;
import io.jenkins.plugins.jacked.install.InstallBinary;
import io.jenkins.plugins.jacked.scanType.ScanType;
import jenkins.model.Jenkins;
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
    private String severityType;
    private Boolean autoInstall;
    private String scanType;

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

    public String getSeverityType() {
        return severityType;
    }

    public void setSeverityType(String severityType) {
        this.severityType = severityType;
    }

    public Boolean getAutoInstall() {
        return autoInstall;
    }

    public void setAutoInstall(Boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public Jacked(String scanDest, String repName, String scanName, String severityType, Boolean autoInstall,
            String scanType) {
        this.scanDest = scanDest;
        this.repName = repName;
        this.scanName = scanName;
        this.severityType = severityType;
        this.autoInstall = autoInstall;
        this.scanType = scanType;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        // Check if Jacked Binary installed on workspace
        String[] cmd = { JACKED };
        if (ExecuteBinary.executeJacked(cmd, workspace, launcher, listener) == 1 || Boolean.TRUE.equals(autoInstall)) {

            // Install Jacked
            try {
                InstallBinary.installJacked(workspace, launcher, listener, env);
            } catch (URISyntaxException e) {
                System.out.println("Error on Install Binary Jacked");
                e.printStackTrace();
            }
        }

        // Modify Jacked command with argument

        if (scanName != null && !scanName.equals("")) {

            // Determine the Arguments
            String[] cmdArgs = ScanType.scanTypeArgs(scanType, severityType, scanName);
            ExecuteBinary.executeJacked(cmdArgs, workspace, launcher, listener);
        } else {
            System.out.println("Please input your scan name");
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

        @POST
        public ListBoxModel doFillSeverityTypeItems() throws AccessDeniedException {
            // Check if the user has the necessary permission
            Jenkins jenkins = Jenkins.get();
            if (!jenkins.hasPermission(Permission.CONFIGURE)) {
                throw new AccessDeniedException("Insufficient permissions");
            }

            // Execute the operation
            LOGGER.log(Level.INFO, "doFillSeverityTypeItems() called");
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

        @POST
        public ListBoxModel doFillScanTypeItems() throws AccessDeniedException {
            // Check if the user has the necessary permission
            Jenkins jenkins = Jenkins.get();
            if (!jenkins.hasPermission(Permission.CONFIGURE)) {
                throw new AccessDeniedException("Insufficient permissions");
            }

            // Execute the operation
            LOGGER.log(Level.INFO, "doFillScanTypeItems() called");
            ListBoxModel items = new ListBoxModel(
                    new Option("-- Select --", ""),
                    new Option("Image", "image"),
                    new Option("Directory", "directory"),
                    new Option("Tar File", "tar"),
                    new Option("SBOM File", "sbom"));
            LOGGER.log(Level.INFO, "Returning ListBoxModel: {0}", items);
            return items;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }
}
