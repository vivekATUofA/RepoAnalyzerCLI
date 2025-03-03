package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4 || !args[0].equals("--repo") || !args[2].equals("--output")) {
            System.out.println("Usage: java -jar RepoAnalyzerCLI-1.0-SNAPSHOT.jar --repo <repo-url> --output <json|md>");
            return;
        }

        String repoUrl = args[1];
        String outputFormat = args[3];

        System.out.println("Cloning repository from: " + repoUrl);

        File repoDir = new File("cloned-repo");
        if (repoDir.exists()) {
            deleteDirectory(repoDir);
        }

        try {
            // Clone repo
            ProcessBuilder pb = new ProcessBuilder("git", "clone", repoUrl, repoDir.getAbsolutePath());
            pb.inheritIO().start().waitFor();

            System.out.println("Repository cloned successfully.");

            // Analyze Java files
            List<File> javaFiles = Files.walk(repoDir.toPath())
                    .map(Path::toFile)
                    .filter(file -> file.isFile() && file.getName().endsWith(".java"))
                    .collect(Collectors.toList());

            List<TestScenario> scenarios = javaFiles.stream()
                    .flatMap(file -> parseFile(file).stream())
                    .collect(Collectors.toList());

            // Generate output
            if (outputFormat.equalsIgnoreCase("json")) {
                writeJsonReport(scenarios);
            } else {
                writeMarkdownReport(scenarios);
            }

            System.out.println("Report generated: test_scenarios." + outputFormat);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<TestScenario> parseFile(File file) {
        try {
            CompilationUnit cu = new JavaParser().parse(file).getResult().orElse(null);
            return cu != null ? ControllerScanner.extractScenarios(cu) : List.of();
        } catch (Exception e) {
            System.err.println("Error parsing file: " + file.getName());
            return List.of();
        }
    }

    private static void writeJsonReport(List<TestScenario> scenarios) {
        try (FileWriter writer = new FileWriter("test_scenarios.json")) {
            writer.write("[\n");
            writer.write(scenarios.stream().map(TestScenario::toString).collect(Collectors.joining(",\n")));
            writer.write("\n]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeMarkdownReport(List<TestScenario> scenarios) {
        try (FileWriter writer = new FileWriter("test_scenarios.md")) {
            writer.write("# Test Scenarios\n\n");
            for (TestScenario scenario : scenarios) {
                writer.write("- **" + scenario.getHttpMethod() + "** " + scenario.getEndpoint() + "\n");
                writer.write("  - " + scenario.getDescription() + "\n\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}
