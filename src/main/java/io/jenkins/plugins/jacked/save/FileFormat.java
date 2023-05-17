package io.jenkins.plugins.jacked.save;

import io.jenkins.plugins.jacked.Jacked;

public class FileFormat {

    public static String fileName() {
        String fileName = Jacked.DescriptorImpl.getDefaultRepName();
        return fileName;

    }
}
