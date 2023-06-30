package io.jenkins.plugins.jacked.os;

import java.io.IOException;

public class Windows {

    public boolean isJackedAvailableWindows() throws IOException, InterruptedException {
        String[] command = { "cmd", "/c", "where", "jacked" };
        Process process = new ProcessBuilder(command).start();
        int exitCode = process.waitFor();
        return (exitCode == 0);
    }
}
