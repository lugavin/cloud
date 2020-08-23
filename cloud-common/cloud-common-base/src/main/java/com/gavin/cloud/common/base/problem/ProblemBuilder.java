package com.gavin.cloud.common.base.problem;

import java.net.URI;
import java.util.*;

public final class ProblemBuilder {

    private static final Set<String> RESERVED_PROPERTIES = new HashSet<>(Arrays.asList(
            "type", "status", "title", "detail", "cause"
    ));

    private URI type;
    private StatusType status;
    private String title;
    private String detail;
    private ThrowableProblem cause;
    private Map<String, Object> parameters;

    ProblemBuilder() {
        this.parameters = new LinkedHashMap<>();
    }

    public ProblemBuilder withType(URI type) {
        this.type = type;
        return this;
    }

    public ProblemBuilder withStatus(StatusType status) {
        this.status = status;
        return this;
    }

    public ProblemBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProblemBuilder withDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public ProblemBuilder withCause(ThrowableProblem cause) {
        this.cause = cause;
        return this;
    }

    public ProblemBuilder with(String key, Object value) {
        if (RESERVED_PROPERTIES.contains(key)) {
            throw new IllegalArgumentException("Property " + key + " is reserved");
        }
        parameters.put(key, value);
        return this;
    }

    public ThrowableProblem build() {
        return new DefaultProblem(type, status, title, detail, cause, parameters);
    }

    private static class DefaultProblem extends AbstractThrowableProblem implements Exceptional {
        DefaultProblem(URI type, StatusType status, String title, String detail,
                       ThrowableProblem cause, Map<String, Object> parameters) {
            super(type, status, title, detail, cause, parameters);
        }
    }

}