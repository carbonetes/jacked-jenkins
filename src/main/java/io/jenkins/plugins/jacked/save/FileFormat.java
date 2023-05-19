package io.jenkins.plugins.jacked.save;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import hudson.model.listeners.RunListener;

@Extension
public class FileFormat extends RunListener<Run<?, ?>> {
    private static String fileName; // Class-level variable to store the file name

    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        String jobName = run.getParent().getFullName();
        int buildNumber = run.getNumber();

        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins != null) {
            // Get the queue
            Queue.Item[] items = jenkins.getQueue().getItems();
            if (items.length > 0) {
                // Get the first item in the queue
                Queue.Item queueItem = items[0];

                // Get the task object associated with the queue item
                Queue.Task task = queueItem.task;
                if (task instanceof Job<?, ?>) {
                    Job<?, ?> queueJob = (Job<?, ?>) task;

                    // Get the current build number from the queue item
                    int currentBuildNumber = (int) queueItem.getId();

                    // Get the job name
                    jobName = queueJob.getFullName();

                    // Update the build number
                    buildNumber = currentBuildNumber;
                }
            }

            // Construct the file name using jobName and buildNumber
            String localFileName = "jacked_result_" + jobName + "_" + buildNumber + ".txt";

            setFileName(localFileName);
        } 
    }
    
    private static void setFileName(String value) {
        fileName = value;
    }
    
    public static String getFileName() {
        return fileName != null ? fileName : "";
    }
}
