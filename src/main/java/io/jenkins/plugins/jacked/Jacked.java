package io.jenkins.plugins.jacked;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

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
import io.jenkins.plugins.jacked.compile.Compile;
import io.jenkins.plugins.jacked.install.Clone;
import io.jenkins.plugins.jacked.install.Go;
import io.jenkins.plugins.jacked.install.InstallBinary;
import io.jenkins.plugins.jacked.install.JackedExist;
import io.jenkins.plugins.jacked.install.Scoop;
import io.jenkins.plugins.jacked.os.CheckOS;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;

import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

/**
 * @author Sairen Christian Buerano - Carbonetes
 */
@Getter
@Setter
public class Jacked extends Builder implements SimpleBuildStep {
    
    private JackedConfig jackedConfig;
    private String scanDest;
    private String scanName;
    private String severityType;
    private String scanType;
    private Boolean skipFail;
    private Boolean skipDbUpdate;
    private String ignorePackageNames;
    private String ignoreCves;
    private String token;
    private Map<String, String> content;

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public Jacked(
        String scanDest, 
        String scanName, 
        String severityType,
        String scanType, 
        Boolean skipFail, 
        Boolean skipDbUpdate, 
        String ignorePackageNames, 
        String ignoreCves, 
        String token,
        Map<String, String> 
        content, 
        JackedConfig 
        jackedConfig
    ){
        this.scanDest = scanDest;
        this.scanName = scanName;
        this.severityType = severityType;
        this.scanType = scanType;
        this.skipFail = skipFail;
        this.skipDbUpdate = skipDbUpdate;
        this.ignorePackageNames = ignorePackageNames;
        this.ignoreCves = ignoreCves;
        this.content = content;
        this.token = token;
        this.jackedConfig = new JackedConfig(
            scanDest, 
            scanName, 
            severityType, 
            scanType, 
            skipFail, 
            skipDbUpdate,
            ignorePackageNames, 
            ignoreCves,
            token
        );
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        // Initiate Jenkins and Input Config Model
        JenkinsConfig jenkinsConfig = new JenkinsConfig(run, workspace, env, launcher, listener);
        // Perform installation based on the OS
        // installJacked(jenkinsConfig);
        clone(jenkinsConfig);
    }
    
    /* 
     Check OS and Install Jacked
     Performs installation / update process of Jacked Binary inside the workspace based on the operating system.
     If has the updated version of the binary, installation / update process will be skipped.
     Unix / Windows
     */
    /*
    public void installJacked(JenkinsConfig jenkinsConfig)
            throws IOException, InterruptedException {
        
        // Call instance method.
        Scoop scoop = new Scoop();
        InstallBinary installBinary = new InstallBinary();
        JackedExist jackedExist = new JackedExist();
    
        String osName = CheckOS.osName();
        jenkinsConfig.getListener().getLogger().println("Jacked Plugin - Running on: " + osName);
    
        if (Boolean.FALSE.equals(jackedExist.checkIfExists(jenkinsConfig.getWorkspace()))) {
            if (Boolean.TRUE.equals(CheckOS.isWindows(osName))) {
                // Windows Installation Process
                scoop.checkScoop(jenkinsConfig, jackedConfig);
            } else {
                // Unix Installation Process
                try {
                    installBinary.installJacked(jenkinsConfig, jackedConfig);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Compile compileArgs = new Compile();
            compileArgs.compileArgs(jenkinsConfig, jackedConfig);
        }
    }
    */

    public void clone(JenkinsConfig jenkinsConfig) throws IOException, InterruptedException {

        JackedExist jackedExist = new JackedExist();
        if (Boolean.FALSE.equals(jackedExist.checkIfExists(jenkinsConfig.getWorkspace()))) {
            Clone.repo(jenkinsConfig);
            Go.install(jenkinsConfig);
        }
        Compile compileArgs = new Compile();
        compileArgs.compileArgs(jenkinsConfig, jackedConfig);


    }    
    /**
     * Pipeline and Buildstep Setup
     */
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
            return "Jacked Vulnerability Analyzer";
        }

        /*
         * Select Option for Fail-criteria.
         * @return ListBoxModel for Severity Type.
         * @throws AccessDeniedException validate user permissions.
         */
        @POST
        public ListBoxModel doFillSeverityTypeItems() throws AccessDeniedException {
            // Check if the user has the necessary permission
            Jenkins jenkins = Jenkins.get();
            if (!jenkins.hasPermission(Permission.CONFIGURE)) {
                throw new AccessDeniedException("Insufficient permissions");
            }
            return new ListBoxModel(
                    new Option("-- Select --", ""),
                    new Option("Critical", "critical"),
                    new Option("High", "high"),
                    new Option("Medium", "medium"),
                    new Option("Low", "low"),
                    new Option("Negligible", "negligible"),
                    new Option("Unknown", "unknown"));
        }

        /*
         * Select option for Scan Type.
         * @return ListBoxModel for Scan Type.
         * @throws AccessDeniedException checked exception thrown when a file system operation is denied, typically due to a file permission or other access check..
         */
        @POST
        public ListBoxModel doFillScanTypeItems() throws AccessDeniedException {
            // Check if the user has the necessary permission
            Jenkins jenkins = Jenkins.get();
            if (!jenkins.hasPermission(Permission.CONFIGURE)) {
                throw new AccessDeniedException("Insufficient permissions");
            }
            return new ListBoxModel(
                    new Option("-- Select --", ""),
                    new Option("Image", "image"),
                    new Option("Directory", "directory"),
                    new Option("Tar File", "tar"),
                    new Option("SBOM File", "sbom"));
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
