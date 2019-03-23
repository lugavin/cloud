package com.gavin.cloud.common.base.problem;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public abstract class ThrowableProblem extends RuntimeException implements Problem, Exceptional {

    protected ThrowableProblem(final ThrowableProblem cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return Stream.of(getTitle(), getDetail())
                .filter(Objects::nonNull)
                .collect(joining(": "));
    }

    @Override
    public ThrowableProblem getCause() {
        // cast is safe, since the only way to set this is our constructor
        return (ThrowableProblem) super.getCause();
    }


}