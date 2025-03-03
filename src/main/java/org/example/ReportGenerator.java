package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ReportGenerator {

    public static void generateJsonReport(List<TestScenario> scenarios, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("[\n");
            writer.write(scenarios.stream().map(TestScenario::toString).collect(Collectors.joining(",\n")));
            writer.write("\n]");
            System.out.println("JSON report generated: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateMarkdownReport(List<TestScenario> scenarios, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Test Scenarios\n\n");
            for (TestScenario scenario : scenarios) {
                writer.write("- **" + scenario.getHttpMethod() + "** " + scenario.getEndpoint() + "\n");
                writer.write("  - " + scenario.getDescription() + "\n\n");
            }
            System.out.println("Markdown report generated: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
