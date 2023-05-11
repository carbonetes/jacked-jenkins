package io.jenkins.plugins.jacked.os;

import hudson.model.TaskListener;

public class CheckOS {
    public static String osName(TaskListener listener) {
        String osName = System.getProperty("os.name");
        listener.getLogger().println("Jacked Plugin - Running on: " + osName);
        return osName;
    }

    public static Boolean isWindows(String osName) {
        return osName.toLowerCase().contains("windows");
    }
}
