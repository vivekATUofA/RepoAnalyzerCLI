package org.example;

public class TestScenario {
    private final String method;
    private final String endpoint;
    private final String description;

    public TestScenario(String method, String endpoint, String description) {
        this.method = method;
        this.endpoint = endpoint;
        this.description = description;
    }

    public String getMethod() { return method; }
    public String getEndpoint() { return endpoint; }
    public String getDescription() { return description; }
}
