package io.jenkins.plugins.jacked.install;

import java.io.IOException;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import io.jenkins.plugins.jacked.Jacked;

public class Scoop {

        // Check Scoop
        public static void checkScoop(FilePath workspace, EnvVars env, Launcher launcher,
                        TaskListener listener,
                        String scanName, String scanType, String severityType, Boolean skipFail,
                        Boolean skipDbUpdate)
                        throws IOException, InterruptedException {

                String[] checkScoop = { "powershell.exe", "scoop -v" };
                Launcher.ProcStarter checkScoopProcStarter = launcher.launch()
                                .cmds(checkScoop)
                                .envs(env).pwd(workspace).stdin(null).stdout(listener).stderr(listener.getLogger());
                int checkScoopRet = checkScoopProcStarter.start().join();

                if (checkScoopRet != 0) {
                        listener.getLogger().println("Scoop is not installed.");
                        listener.getLogger().println("Preparing to install scoop...");
                        installScoop(workspace, env, launcher, listener, scanName, scanType, severityType, skipFail,
                                        skipDbUpdate);
                } else {
                        listener.getLogger().println("Preparing to install jacked via scoop...");
                        installJacked(workspace, env, launcher, listener, scanName, scanType, severityType, skipFail,
                                        skipDbUpdate);
                }
        }

        // Scoop Install
        public static void installScoop(FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener,
                        String scanName, String scanType, String severityType, Boolean skipFail,
                        Boolean skipDbUpdate)
                        throws IOException, InterruptedException {

                listener.getLogger().println("Installing Scoop...");
                Process process = new ProcessBuilder("powershell.exe",
                                "Set-ExecutionPolicy RemoteSigned -scope CurrentUser; iwr -useb get.scoop.sh | iex")
                                .redirectErrorStream(true)
                                .start();
                process.waitFor();
                listener.getLogger().println("Scoop installation finished");
                installJacked(workspace, env, launcher, listener, scanName, scanType, severityType, skipFail,
                                skipDbUpdate);

        }

        // Scoop Install Jacked
        public static void installJacked(FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener,
                        String scanName, String scanType, String severityType, Boolean skipFail,
                        Boolean skipDbUpdate)
                        throws IOException, InterruptedException {

                // Clean up jacked bucket
                String[] bucket = { "powershell", "scoop bucket rm jacked" };
                cleanUpJacked(bucket, workspace, env, launcher, listener);
                String[] app = { "powershell", "scoop uninstall jacked" };
                cleanUpJacked(app, workspace, env, launcher, listener);

                listener.getLogger().println("Preparing to install jacked via scoop...");

                // Add Jacked bucket to Scoop
                String[] addBucket = { "powershell",
                                "scoop bucket add jacked https://github.com/carbonetes/jacked-bucket" };
                procCommand(addBucket, workspace, env, launcher, listener);
                // Install/Update Jacked using Scoop
                String[] installJacked = { "powershell", "scoop install jacked" };
                procCommand(installJacked, workspace, env, launcher, listener);

                listener.getLogger().println("Jacked Installed Successfully");

                // Start compiling arguments for scanning
                Jacked.compileArgs(workspace, env, launcher, listener, scanName, scanType, severityType,
                                skipFail, skipDbUpdate);

        }

        public static void procCommand(String[] args, FilePath workspace, EnvVars env, Launcher launcher,
                        TaskListener listener) throws IOException, InterruptedException {

                Launcher.ProcStarter proc = launcher.launch()
                                .cmds(args)
                                .envs(env).pwd(workspace).stdin(null).stdout(listener).stderr(listener.getLogger());
                int procCode = proc.join();
                if (procCode != 0) {
                        listener.getLogger()
                                        .println("Failed to execute command - error code: " + procCode);
                        return;
                }
        }

        public static void cleanUpJacked(String[] args, FilePath workspace, EnvVars env, Launcher launcher,
                        TaskListener listener) throws IOException, InterruptedException {
                Launcher.ProcStarter cleanBucket = launcher.launch()
                                .cmds(args)
                                .envs(env).pwd(workspace).stdin(null).stdout(listener).stderr(listener.getLogger());
                cleanBucket.join();

        }

}
