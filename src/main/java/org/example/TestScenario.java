package org.example;

public class TestScenario {
    private final String httpMethod;
    private final String endpoint;
    private final String description;

    public TestScenario(String httpMethod, String endpoint, String description) {
        this.httpMethod = httpMethod;
        this.endpoint = endpoint;
        this.description = description;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "{ \"httpMethod\": \"" + httpMethod + "\", \"endpoint\": \"" + endpoint + "\", \"description\": \"" + description + "\" }";
    }
}
