package io.jenkins.plugins.jacked.os;

import java.io.IOException;

public class Unix {
    public static boolean isJackedAvailableUnix() throws IOException, InterruptedException {
        String[] command = { "which", "jacked" };
        Process process = new ProcessBuilder(command).start();
        int exitCode = process.waitFor();
        return (exitCode == 0);
    }
}
