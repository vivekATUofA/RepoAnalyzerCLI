package org.example;

import java.io.File;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar RepoAnalyzerCLI.jar --repo <git-url> --output <json|md>");
            System.exit(1);
        }

        String repoUrl = args[1];
        String outputFormat = args[3];

        File repoDir = Paths.get("temp-repo").toFile();
        RepoCloner.cloneRepository(repoUrl, repoDir);

        ControllerScanner scanner = new ControllerScanner();
        scanner.analyzeControllers(repoDir);

        ReportGenerator.generateReport(scanner.getExtractedScenarios(), outputFormat);
    }
}
