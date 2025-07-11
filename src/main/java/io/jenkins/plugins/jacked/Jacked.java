package io.jenkins.plugins.jacked;

import java.io.IOException;
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
import io.jenkins.plugins.jacked.install.CarbonetesCI;
import io.jenkins.plugins.jacked.install.Exist;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;

import io.jenkins.plugins.jacked.model.JackedConfig;
import io.jenkins.plugins.jacked.model.JenkinsConfig;

/**
 * @author Sairen Christian Buerano
 * @author Carbonetes
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
        this.content = content;
        this.token = token;
        this.jackedConfig = new JackedConfig(
            scanDest, 
            scanName, 
            severityType, 
            scanType, 
            skipFail, 
            token
        );
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        // Initiate Jenkins and Input Config Model
        JenkinsConfig jenkinsConfig = new JenkinsConfig(run, workspace, env, launcher, listener);
        setup(jenkinsConfig);
    }

    public void setup(JenkinsConfig jenkinsConfig) throws IOException, InterruptedException {
        
        Exist jackedExist = new Exist();
        if (Boolean.FALSE.equals(jackedExist.checkIfExists(jenkinsConfig.getWorkspace()))) {
            CarbonetesCI.install(jenkinsConfig, jackedConfig);
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
                new Option("Tar File", "tar")
            );
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
