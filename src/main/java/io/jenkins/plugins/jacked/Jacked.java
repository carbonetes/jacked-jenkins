package io.jenkins.plugins.jacked;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.Symbol;
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
import io.jenkins.plugins.jacked.install.JackedExist;
import io.jenkins.plugins.jacked.install.Scoop;
import io.jenkins.plugins.jacked.os.CheckOS;
import io.jenkins.plugins.jacked.scan.SetArgs;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

public class Jacked extends Builder implements SimpleBuildStep {
    private static final Logger LOGGER = Logger.getLogger(Jacked.class.getName());

    private String scanDest;
    private String repName;
    private String scanName;
    private String severityType;
    private String scanType;
    private Boolean skipFail;
    private Boolean skipDbUpdate;
    private String ignorePackageNames;
    private String ignoreCves;

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

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public Boolean getSkipFail() {
        return skipFail;
    }

    public void setSkipFail(Boolean skipFail) {
        this.skipFail = skipFail;
    }

    public Boolean getSkipDbUpdate() {
        return skipDbUpdate;
    }

    public void setSkipDbUpdate(Boolean skipDbUpdate) {
        this.skipDbUpdate = skipDbUpdate;
    }

    public String getIgnorePackageNames() {
        return ignorePackageNames;
    }

    public void setIgnorePackageNames(String ignorePackageNames) {
        this.ignorePackageNames = ignorePackageNames;
    }
    
    public String getIgnoreCves() {
        return ignoreCves;
    }

    public void setIgnoreCves(String ignoreCves) {
        this.ignoreCves = ignoreCves;
    }

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public Jacked(String scanDest, String repName, String scanName, String severityType,
            String scanType, Boolean skipFail, Boolean skipDbUpdate, String ignorePackageNames, String ignoreCves) {
        this.scanDest = scanDest;
        this.repName = repName;
        this.scanName = scanName;
        this.severityType = severityType;
        this.scanType = scanType;
        this.skipFail = skipFail;
        this.skipDbUpdate = skipDbUpdate;
        this.ignorePackageNames = ignorePackageNames;
        this.ignoreCves = ignoreCves;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        String osName = CheckOS.osName();
        // Jacked Running on this OS.
        listener.getLogger().println("Jacked Plugin - Running on: " + osName);

        // Check OS
        // Auto install, check jacked exists on workspace, if true skip install
        if (Boolean.FALSE.equals(JackedExist.checkIfExists(workspace))) {
            if (CheckOS.isWindows(osName)) {
                // Windows specific action
                Scoop.checkScoop(workspace, env, launcher, listener, scanName, scanType, severityType, skipFail,
                        skipDbUpdate, ignorePackageNames, ignoreCves);
            } else {
                try {
                    InstallBinary.installJacked(workspace, launcher, listener, env, scanName, scanType,
                            severityType,
                            skipFail, skipDbUpdate, ignorePackageNames, ignoreCves);
                } catch (URISyntaxException e) {
                    e.printStackTrace();

                }
            }
        } else {
            compileArgs(workspace, env, launcher, listener, scanName, scanType, severityType, skipFail,
                    skipDbUpdate, ignorePackageNames, ignoreCves);
        }
    }

    public static void compileArgs(FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener,
            String scanName, String scanType, String severityType, Boolean skipFail,
            Boolean skipDbUpdate, String ignorePackageNames, String ignoreCves)
            throws InterruptedException, IOException {

        // Modify Jacked command with argument
        if (scanName != null && !scanName.equals("")) {

            // Determine the Arguments
            String[] cmdArgs = SetArgs.scanTypeArgs(scanType, severityType, scanName, skipDbUpdate, ignorePackageNames, ignoreCves);
            ExecuteBinary.executeJacked(cmdArgs, workspace, launcher, listener, skipFail);
        } else {
            listener.getLogger().println("Please input your scan name");
        }
    }

    public static String time() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmm");
        String timestamp = formatter.format(currentDate);

        return timestamp;

    }

    @Symbol("jacked")
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

        @Override
        public String getDisplayName() {
            return "Vulnerability scan with jacked";
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
