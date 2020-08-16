package com.gavin.cloud.common.base.exception;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ThrowableProblem extends RuntimeException implements Problem {

    protected ThrowableProblem(final ThrowableProblem cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return Stream.of(getTitle(), getDetail())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(": "));
    }

    @Override
    public ThrowableProblem getCause() {
        // cast is safe, since the only way to set this is our constructor
        return (ThrowableProblem) super.getCause();
    }

}