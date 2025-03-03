package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerScanner {

    public static List<TestScenario> extractScenarios(CompilationUnit cu) {
        List<TestScenario> scenarios = new ArrayList<>();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            // Extract class-level mapping
            Optional<String> classMapping = extractMapping(classDecl.getAnnotationByName("RequestMapping"));

            classDecl.getMethods().forEach(method -> {
                method.getAnnotations().forEach(annotation -> {
                    String annotationName = annotation.getNameAsString();

                    if (isMappingAnnotation(annotationName)) {
                        // Extract method-level mapping
                        Optional<String> methodMapping = extractMapping(Optional.of(annotation));
                        String endpoint = combinePaths(classMapping.orElse(""), methodMapping.orElse(""));

                        scenarios.add(new TestScenario(getHttpMethod(annotationName), endpoint, "Verify successful request processing"));
                    }
                });
            });
        });

        return scenarios;
    }

    private static Optional<String> extractMapping(Optional<AnnotationExpr> annotation) {
        if (annotation.isEmpty()) return Optional.empty();

        AnnotationExpr expr = annotation.get();
        if (expr instanceof SingleMemberAnnotationExpr single) {
            return Optional.of(single.getMemberValue().toString().replaceAll("\"", ""));
        } else if (expr instanceof NormalAnnotationExpr normal) {
            return normal.getPairs().stream()
                    .filter(p -> p.getNameAsString().equals("value"))
                    .map(p -> p.getValue().toString().replaceAll("\"", ""))
                    .findFirst();
        }
        return Optional.empty();
    }

    private static String getHttpMethod(String annotationName) {
        return switch (annotationName) {
            case "GetMapping" -> "GET";
            case "PostMapping" -> "POST";
            case "PutMapping" -> "PUT";
            case "DeleteMapping" -> "DELETE";
            default -> "UNKNOWN";
        };
    }

    private static boolean isMappingAnnotation(String annotationName) {
        return annotationName.equals("GetMapping") || annotationName.equals("PostMapping") ||
                annotationName.equals("PutMapping") || annotationName.equals("DeleteMapping") ||
                annotationName.equals("RequestMapping");
    }


    private static String combinePaths(String classPath, String methodPath) {
        if (classPath.endsWith("/") && methodPath.startsWith("/")) {
            return classPath + methodPath.substring(1); // Avoid double slashes
        }
        return classPath + methodPath;
    }
}
