package io.jenkins.plugins.jacked.install;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CheckVersion {

    public static String getVersion() {
        String repositoryUrl = "https://github.com/carbonetes/jacked";
        try {
            String latestVersion = getLatestVersion(repositoryUrl);
            return latestVersion;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLatestVersion(String repositoryUrl) throws IOException {
        String apiUrl = repositoryUrl.replace("github.com", "api.github.com/repos") + "/releases/latest";
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        try (InputStream inputStream = connection.getInputStream()) {
            Scanner scanner = new Scanner(inputStream, "UTF-8");  // Specify UTF-8 encoding
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse the JSON response to extract the latest version
            // Assuming the response is in the format: {"tag_name": "vX.X.X"}
            return response.split("\"tag_name\":")[1].split("\"")[1];
        }
    }
}