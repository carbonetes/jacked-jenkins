package io.jenkins.plugins.jacked.os;

public class CheckOS {
    public static String osName() {
        return System.getProperty("os.name");
    }

    public static Boolean isWindows(String osName) {
        return osName.toLowerCase().contains("windows");
    }
}
