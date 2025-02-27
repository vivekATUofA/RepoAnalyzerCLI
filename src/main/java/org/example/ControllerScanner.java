package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ControllerScanner {
    private final List<TestScenario> extractedScenarios = new ArrayList<>();

    public void analyzeControllers(File repoDir) {
        scanDirectory(repoDir);
    }

    private void scanDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file);
                } else if (file.getName().endsWith(".java")) {
                    analyzeJavaFile(file);
                }
            }
        }
    }

    private void analyzeJavaFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            CompilationUnit cu = new JavaParser().parse(fis).getResult().orElse(null);
            if (cu == null) return;

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                List<TestScenario> scenarios = ScenarioExtractor.extractTestScenarios(method);
                extractedScenarios.addAll(scenarios);
            });
        } catch (Exception e) {
            System.err.println("Error analyzing file " + file.getName() + ": " + e.getMessage());
        }
    }

    public List<TestScenario> getExtractedScenarios() {
        return extractedScenarios;
    }
}
