package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.TestScenario;

public class ScenarioExtractor {
    public static List<TestScenario> extractTestScenarios(MethodDeclaration method) {
        List<TestScenario> scenarios = new ArrayList<>();

        Optional<AnnotationExpr> mappingAnnotation = method.getAnnotations().stream()
                .filter(a -> a.getName().asString().matches("GetMapping|PostMapping|PutMapping|DeleteMapping"))
                .findFirst();

        if (mappingAnnotation.isPresent()) {
            String httpMethod = mappingAnnotation.get().getName().asString().replace("Mapping", "").toUpperCase();
            String endpoint = method.getAnnotationByName(mappingAnnotation.get().getName().asString()).toString();

            // Add basic scenario
            scenarios.add(new TestScenario(httpMethod, endpoint, "Verify successful request processing"));

            // Security test cases
            if (method.getAnnotationByName("PreAuthorize").isPresent() ||
                    method.getAnnotationByName("Secured").isPresent()) {
                scenarios.add(new TestScenario(httpMethod, endpoint, "Verify authentication & authorization"));
            }

            // Error handling cases
            if (method.getThrownExceptions().size() > 0) {
                scenarios.add(new TestScenario(httpMethod, endpoint, "Verify exception handling"));
            }

            // Input validation
            if (method.getParameters().stream().anyMatch(p -> p.isAnnotationPresent("Valid"))) {
                scenarios.add(new TestScenario(httpMethod, endpoint, "Check request validation constraints"));
            }
        }

        return scenarios;
    }
}
