package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {
    public static void generateReport(List<TestScenario> scenarios, String format) {
        if (format.equalsIgnoreCase("json")) {
            generateJsonReport(scenarios);
        } else {
            generateMarkdownReport(scenarios);
        }
    }

    private static void generateJsonReport(List<TestScenario> scenarios) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("test_scenarios.json")) {
            gson.toJson(scenarios, writer);
            System.out.println("JSON report generated: test_scenarios.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateMarkdownReport(List<TestScenario> scenarios) {
        try (FileWriter writer = new FileWriter("test_scenarios.md")) {
            for (TestScenario scenario : scenarios) {
                writer.write("- **" + scenario.getMethod() + "** `" + scenario.getEndpoint() + "`: " + scenario.getDescription() + "\n");
            }
            System.out.println("Markdown report generated: test_scenarios.md");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
