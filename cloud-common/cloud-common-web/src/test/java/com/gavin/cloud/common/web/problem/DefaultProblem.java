package com.gavin.cloud.common.web.problem;

import java.net.URI;
import java.util.Map;

public final class DefaultProblem extends AbstractThrowableProblem {

    DefaultProblem(final URI type,
                   final String title,
                   final StatusType status,
                   final String detail,
                   final URI instance,
                   final ThrowableProblem cause) {
        super(type, title, status, detail, instance, cause);
    }

    DefaultProblem(final URI type,
                   final String title,
                   final StatusType status,
                   final String detail,
                   final URI instance,
                   final ThrowableProblem cause,
                   final Map<String, Object> parameters) {
        super(type, title, status, detail, instance, cause, parameters);
    }

}